clojurescript.test fails if no test file
========================================

If there is (even an empty) test file, `clojurescript.test` will work just fine:

    $ lein do clean, test-all 
    ...
    Compiling ClojureScript.
    Compiling "target/cljs/testable.js" from ["target/classes/cljs" "target/test-classes"]...
    ...
    Successfully compiled "target/cljs/testable.js" in 13.301 seconds.
    Running ClojureScript test: unit-tests

    Ran 0 tests containing 0 assertions.
    Testing complete: 0 failures, 0 errors.

However, if there are no test files (if we remove "target/test-classes" from `cljsbuild`'s `:source-paths`), the test will fail with a completely confusing error:

     $ lein do clean, test-all
     ...
     Compiling ClojureScript.
     Compiling "target/cljs/testable.js" from ["target/classes/cljs"]...
     ...
     Successfully compiled "target/cljs/testable.js" in 12.297652 seconds.
     Running ClojureScript test: unit-tests
     ReferenceError: Can't find variable: cemerick

       phantomjs://webpage.evaluate():2
       phantomjs://webpage.evaluate():5
       phantomjs://webpage.evaluate():5

(It fails similarly, though with little more [still useless] info with the `node` runner.)

The failure is likely due to the fact that, having no test files, the `cemerick.cljs.test`
namespace isn't included anywhere and thus is not present in the compiled `.js`. So the failure
is not surprising however the test runner should do a much better job of detecting that there are
no test classes and reporting it in a meaningful way.
