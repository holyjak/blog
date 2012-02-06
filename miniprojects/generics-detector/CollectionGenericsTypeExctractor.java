/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singleton;

/**
 * Parse Java classes and log the type of elements of generics collections and maps returned from getters.
 *
 * <h3>Current limitations</h3>
 * <ul>
 *     <li>Doesn't take into account inherited getters</li>
 *     <li>Not very fail-proof</li>
 *     <li>Doesn't recognize Collection/Map implementations that are nested/inner classes</li>
 * </ul>
 *
 * @see <a href="See http://today.java.net/pub/a/today/2008/04/10/source-code-analysis-using-java-6-compiler-apis.html">
 *     Source Code Analysis Using Java 6 APIs</a> by Seema Richard, 4/2008 - most of the code taken from here
 * @see <a href="http://crazyjavahacking.org/the-story-of-javac-ast-visualization-on-netbeans-platform/">
 *     The story of javac AST Visualization on NetBeans Platform</a> - blog and open-source project - some of its code might be useful
 */
public class CollectionGenericsTypeExctractor {

	/**
	 * Sun Java Compiler Tree API visitor that finds all getters returning maps/collections with generics
	 * and logs type of values they declare to contain.
	 */
	public static class CodeAnalyzerTreeVisitor extends TreePathScanner<Object, Trees> {

		/**
		 * Category of type returned by a getter - only collections and maps are interesting for us.
		 */
		private static enum TypeCategory {
			COLLECTION(0), MAP(1), OTHER(-1);

			private final int valueTypeArgumentIdx;

			private TypeCategory(int valueTypeArgumentIdx) {
				this.valueTypeArgumentIdx = valueTypeArgumentIdx;
			}

			public boolean isCollectionOrMap() {
				return !this.equals(OTHER);
			}

			/**
			 * Which type argument of this type is used for values?
			 * For collection it's the first and only one,
			 * for maps it's the second one (first is used for keys).
			 */
			public int getValueTypeArgumentIdx() {
				return valueTypeArgumentIdx;
			}
		}

		@Override
		public Object visitMethod(MethodTree methodTree, Trees trees) {
			String typeNameQualified = getEnclosingClassNameIfAvailable(trees);

			// Skip or bad stuff happens
			if (typeNameQualified == null) {
				return super.visitMethod(methodTree, trees);
			}

			Tree returnType = methodTree.getReturnType();   // null for void method
			if (getter(methodTree) && returnType instanceof ParameterizedTypeTree) {
				assert Tree.Kind.PARAMETERIZED_TYPE == returnType.getKind();
				ParameterizedTypeTree parametrizedReturnType = (ParameterizedTypeTree) returnType;

				TypeCategory category = detectTypeCategory(parametrizedReturnType);
				if (category.isCollectionOrMap()) {
					Tree valueTypeArgument = parametrizedReturnType.getTypeArguments().get(category.getValueTypeArgumentIdx());
					final String qualifiedGenericTypeName = getQualifiedType(valueTypeArgument);

					String methodJsfName = getMethodJsfName(methodTree);
					System.out.println("FOUND " + typeNameQualified + "." + methodJsfName + ".*=" + qualifiedGenericTypeName);
					// Unqualified name:
					// assert Tree.Kind.IDENTIFIER == valueTypeArgument.getKind(); IdentifierTree typeIdentifier = (IdentifierTree) valueTypeArgument;
					// typeIdentifier.getName().toString();
				}
			}
			return super.visitMethod(methodTree, trees);
		}

		private String getEnclosingClassNameIfAvailable(Trees trees) {
			// Method element is enclosed by its class, I suppose
			try {
				TypeElement typeElement = (TypeElement) trees.getElement(getCurrentPath()).getEnclosingElement();
				return typeElement.getQualifiedName().toString();
			} catch (NullPointerException e) {
				// getElement will return null for an inner anonymous class
				return null;
			}
		}

		private String getQualifiedType(/*IdentifierTree*/Tree typeArgument) {
			// Ugly hack; without this we could get only typeArgument.getName().toString() bu it is
			// usually unqualified and I've no idea how to use the API to look up the fully qualified name
			return ((JCTree) typeArgument).type.toString();
		}

		private TypeCategory detectTypeCategory(ParameterizedTypeTree parametrizedReturnType) {
			// BEWARE: Doesn't work for nested classes (we'd need to replace the last '.' with '$'
			String qualifiedReturnType = getQualifiedType((IdentifierTree) parametrizedReturnType.getType());
			try {
				Class<?> returnClazz = Class.forName(qualifiedReturnType);
				if (Collection.class.isAssignableFrom(returnClazz)) {
					return TypeCategory.COLLECTION;
				} else if (Map.class.isAssignableFrom(returnClazz)) {
					return TypeCategory.MAP;
				}
			} catch (ClassNotFoundException e) {
				System.err.println("detectTypeCategory: Failed to load class for '" + qualifiedReturnType + "', ignoring");
			}

			return TypeCategory.OTHER;
		}

		private static boolean getter(MethodTree methodTree) {
			final String methodName = methodTree.getName().toString();
			return methodTree.getModifiers().getFlags().contains(Modifier.PUBLIC) &&
					methodTree.getParameters().size() == 0 &&
					methodName.length() > 3 &&
					methodName.startsWith("get");
		}

		/** For "getName" return "name". */
		private static String getMethodJsfName(MethodTree methodTree) {
			String nameWithoutGet = methodTree.getName().toString().substring(3);
			return nameWithoutGet.substring(0, 1).toLowerCase() + nameWithoutGet.substring(1);
		}

	}

	/**
	 * Custom annotation processor to run our visitor
	 */
	@SupportedSourceVersion(SourceVersion.RELEASE_6)
	@SupportedAnnotationTypes("*")
	public static class CodeAnalyzerProcessor extends AbstractProcessor {

		private final CodeAnalyzerTreeVisitor visitor = new CodeAnalyzerTreeVisitor();
		private Trees trees;

		@Override
		public void init(ProcessingEnvironment pe) {
			super.init(pe);
			trees = Trees.instance(pe);
		}

		@Override
		public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
			for (Element e : roundEnvironment.getRootElements()) {
				// Normally the ellement should represent a class
				TreePath tp = trees.getPath(e);
				// invoke the scanner
				visitor.scan(tp, trees);
			}
			return true;    // handled, don't invoke other processors
		}
	}

	/**
	 * Detect types of elements of generic collections/maps in classes in the provided directory.
	 * Required argument: the directory to search (might be relative path).
	 * BEWARE: It won't work if the sources cannot be compiled, i.e. if the dependencies aren't on the classpath.
	 * <p>
	 *     Set the system property sourcefind.debug to get more info.
	 * </p>
	 * <p>
	 *     NOTE: You need to have Sun's tool.jar on the classpath (i.e. run this with JDK, not JRE).
	 * </p>
	 *
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			throw new IllegalArgumentException("Exactly one argument required: the root directory to search for source codes");
		}

		File searchDir = new File(args[0]);
		if (!searchDir.isDirectory()) {
			throw new IllegalArgumentException("The provided path is not a directory: " + searchDir.getAbsolutePath());
		}

		System.out.println(">>> Going to search Java files under " + searchDir);

		//Get an instance of java compiler
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		//Get a new instance of the standard file manager implementation
		StandardJavaFileManager fileManager = compiler.
				getStandardFileManager(null, null, null);

		// Get the list of java file objects, in this case we have only
		// one file, TestClass.java

		System.out.println(">>> DETECTING SOURCES");
		fileManager.setLocation(StandardLocation.SOURCE_PATH, singleton(searchDir.getAbsoluteFile()));
		Iterable<JavaFileObject> sources = fileManager.list(
				StandardLocation.SOURCE_PATH //locationFor("src/test/java")
				, ""
				, singleton(JavaFileObject.Kind.SOURCE)
				, true);

		if (System.getProperty("sourcefind.debug", null) != null) {
			System.out.println(">>> Sources found:\n" + sources);
		}

		// Create the compilation task
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, sources);

		// Set the annotation processor to the compiler task
		task.setProcessors(singleton(new CodeAnalyzerProcessor()));

		// Perform the compilation task.
		System.out.println(">>> DETECTING GENERICS");
		task.call();

		System.out.println(">>>> DONE DETECTING GENERICS!");
	}
}
