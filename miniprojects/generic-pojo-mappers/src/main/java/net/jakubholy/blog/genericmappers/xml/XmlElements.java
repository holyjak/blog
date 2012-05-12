package net.jakubholy.blog.genericmappers.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Represents parsed XML and provides access to its content.
 * <p>
 * See the JavaDoc of {@link GenericXmlToBeanParser} and its test for more information.
 */
public class XmlElements {

    static final BeanValidatingUnmarshalListener BEAN_VALIDATOR =
            new BeanValidatingUnmarshalListener();

    /**
     * Cache of XPath expressions for efficiency reasons. Hidden insight ThreadLocal because XPathExpressions aren't
     * thread safe.
     */
    private final ThreadLocal<Map<String, XPathExpression>> pathCache =
            new ThreadLocal<Map<String, XPathExpression>>() {
                @Override
                protected Map<String, XPathExpression> initialValue() {
                    return new HashMap<String, XPathExpression>();
                }
            };

    private final Document parsedXmlDocument;
    /** A part of the input XML, for logging (we don't want log all as it might be very long) */
    private String xmlStart;
    private final String rootNodesXpath;

    /**
     * Map XML elements denoted by the xpath supplied via {@link #atXPath(String)} to Java beans of the provided type.
     * <p>
     * See JavaDoc of {@link GenericXmlToBeanParser} for details and examples.
     *
     * @param beanType
     *            (required) the Java class to map the XML elements to
     * @return Non-null but possibly empty collection of beans corresponding to the matching XML elements (also empty if
     *         no matching elements have been found there i.e. due to incorrect XPath and/or namespace configuration)
     */
    public <T> Collection<T> getBeans(Class<T> beanType) {

        if (rootNodesXpath == null) {
            throw new UnsupportedOperationException("No xpath was set, sorry we do not " +
                    "support extracting the root element directly yet; please set its xpath " +
                    "and/or compalin to the developer");
        }

        Collection<T> parsed = new LinkedList<T>();
        NodeList entries = getNodesMatchingXPath(rootNodesXpath);

        if (entries.getLength() == 0) {
            GenericXmlToBeanParser.log.info("No matching elements found for '" + rootNodesXpath + "' in the xml '" +
                    xmlStart + "'");
        }

        for (int i = 0; i < entries.getLength(); i++) {
            Node currentNode = entries.item(i);
            parsed.add(
                    mapNodeToPojo(currentNode, beanType));
        }

        return parsed;
    }

    /**
     * Indicates that the next call to {@link #getBeans(Class)} should extract elements matched by the given XPath
     * expression.
     *
     * <h4>Note: XPath and namespaces</h4> The easiest is to ignore namespaces (whether explicit or implicit) by using
     * "/*[local-name()='myElementName']" instead of "/myElementName" of "/myNsPrefix:myElementName". However we could
     * also experiment with {@link XPath#setNamespaceContext(javax.xml.namespace.NamespaceContext)}, see
     * org.springframework.util.xml.SimpleNamespaceContext in Spring 3.0 and its usage.
     *
     * @param xpathExpression
     *            (required) XPath denoting the XML elements that should be mapped to POJO classes; ex.: '/',
     *            '/feed/entry', /*[local-name()='myElementName'], //anywhereLocatedElm
     * @return A new instance with its root xpath set
     *
     * @see XPath#compile(String)
     */
    public XmlElements atXPath(String xpathExpression) {
        return new XmlElements(this, xpathExpression);
    }

    /**
     * Get the value of the XML element indicated by the given XPath. (Beware namespaces - perhaps use local-name().)
     * <p>
     * Example: xml: {@code <root><myElm>value</myElme></root>}, code:
     * <code>assertEquals("value", xe.getElementValueAtXPath("/root/value"));</code>
     *
     * @param xpathExpression
     *            (required) see {@link XPath#compile(String)}
     * @return The value of the matching XML or null if not found
     * @see #atXPath(String)
     */
    public String getElementValueAtXPath(String xpathExpression) {
        return getNodesMatchingXPath(
                xpathExpression
                , XPathConstants.STRING
                , String.class);
    }

    /**
     * Parse XML into elements.
     *
     * @param xml
     *            (required) the XML to parse
     */
    XmlElements(String xml) {
        this(parseXmlToDocument(xml)
                , extractXmlSample(xml)
                , null);
    }

    /** Copy constructor changing the xpath. */
    private XmlElements(XmlElements original, String xpath) {
        this(original.parsedXmlDocument, original.xmlStart, xpath);
    }

    private XmlElements(Document parsedXmlDocument, String xmlStart, String xpath) {
        this.parsedXmlDocument = parsedXmlDocument;
        this.xmlStart = xmlStart;
        this.rootNodesXpath = xpath;
    }

    /**
     * Get an extract of the input XML long enough to contain useful info for troubleshooting but not too long so that
     * logs do not explode in size.
     */
    private static String extractXmlSample(String xml) {
        return xml.substring(0, Math.min(300, xml.length())) + "...";
    }

    private NodeList getNodesMatchingXPath(String xpathExpression) {
        return getNodesMatchingXPath(xpathExpression, XPathConstants.NODESET, NodeList.class);
    }

    private <T> T getNodesMatchingXPath(String xpathExpression, QName expectedResult, Class<T> resultType) {
        try {
            XPathExpression nodesXPath = makeXPathExpression(xpathExpression);
            @SuppressWarnings("unchecked")
            T result = (T) nodesXPath.evaluate(parsedXmlDocument, expectedResult);
            return result;
        } catch (XPathExpressionException e) {
            // TODO Throw a more specific runtime exception, declare it in the signature
            throw new RuntimeException("Invalid xpath '" + xpathExpression + "': " + e.getMessage(), e);
        }
    }

    XPathExpression makeXPathExpression(String xpathExpression) throws XPathExpressionException {

        XPathExpression nodesXPath = pathCache.get().get(xpathExpression);
        if (nodesXPath == null) {
            XPath xpath = XPathFactory.newInstance().newXPath();
            nodesXPath = xpath.compile(xpathExpression);
            pathCache.get().put(xpathExpression, nodesXPath);
        }
        return nodesXPath;
    }

    @SuppressWarnings("unchecked")
    private <T> T mapNodeToPojo(Node entryNode, Class<T> entryPojoType) {

        try {
            JAXBContext jaxb = JAXBContext.newInstance(entryPojoType); // TODO: Thread-safe => TODO cache it
            Unmarshaller unmarshaller = jaxb.createUnmarshaller(); // Not thread-safe, can't be cached
            unmarshaller.setListener(BEAN_VALIDATOR);

            // Called for unknown elements however not for missing required ones:
            unmarshaller.setEventHandler(new UnknownElementLoggingHandler());

            return (T) unmarshaller.unmarshal(entryNode);
        } catch (JAXBException e) {
            throw new RuntimeException("Mapping XML to Java Bean failed for " +
                    entryPojoType.getName() + " and XML " + xmlStart + ": " +
                    e.getMessage()
                    , e);
        }
    }

    private static Document parseXmlToDocument(String xml) throws RuntimeException {
        // Not thread safe - http://stackoverflow.com/questions/9828254/is-documentbuilderfactory-thread-safe-in-java-5
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        // Without namespace aware some elements with a namespace wouldn't be recognized
        // (node's localName and namespaceUri would be null, nodeName qualified)
        builderFactory.setNamespaceAware(true); // TODO cache the

        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            return builder.parse(is);
        } catch (SAXException e) {
            // TODO Throw a custom runtime exception, e.g. XmlParsingFailedException
            throw new RuntimeException("Parsing XML failed: " + e + ", xml: " + xml, e);
        } catch (IOException e) {
            throw new RuntimeException("Parsing XML failed: " + e + ", xml: " + xml, e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Parsing XML failed: " + e + ", xml: " + xml, e);
        }
    }

}