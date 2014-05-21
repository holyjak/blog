(defproject testtest "1.0.0"
  :url "http://theholyjava.wordpress.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj" "target/classes/clj" "target/classes/cljs"]
  :test-paths ["test" "target/test-classes"]

  :dependencies
  [[org.clojure/clojure "1.6.0"]]

  :profiles
  {:dev {:dependencies [[org.clojure/clojurescript "0.0-2202"]
                        [com.cemerick/piggieback "0.1.3"]]
         :plugins [[com.cemerick/austin "0.1.3"]
                   #_[codox "0.6.4"]
                   [lein-cljsbuild "1.0.3"]
                   [com.keminglabs/cljx "0.3.2"]
                   [com.cemerick/clojurescript.test "0.3.0"]] ; 0.2.1. fails too
         :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :aliases {"test-all" ["do" "clean," "cljx" "once," "test," "cljsbuild" "test"]}

  :cljx
  {:builds [{:source-paths ["src/cljx"]
             :output-path "target/classes/clj"
             :rules :clj}
            {:source-paths ["src/cljx"]
             :output-path "target/classes/cljs"
             :rules :cljs}
            {:source-paths ["test"]
             :output-path "target/test-classes"
             :rules :clj}
            {:source-paths ["test"]
             :output-path "target/test-classes"
             :rules :cljs}]}

  :cljsbuild
  {:builds [{:source-paths ["target/classes/cljs" #_"target/test-classes"]
             :compiler {:output-to "target/cljs/testable.js"
                        :optimizations :whitespace
                        :pretty-print true}}]
   :test-commands {"unit-tests" ["phantomjs" 
                                 :runner 
                                 "target/cljs/testable.js"]}}
)
