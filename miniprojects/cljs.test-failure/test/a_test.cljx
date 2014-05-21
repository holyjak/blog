(ns a-test
  #+clj
  (:require [clojure.test :refer :all])

  #+cljs
  (:require [cemerick.cljs.test :as t])
  #+cljs
  (:require-macros [cemerick.cljs.test :refer [deftest is testing]]))

#_(deftest rule-test
  (testing "rule"
    (is (= ((rule "a") {:text-decoration "none"})
           ["a" {:text-decoration "none"}]))
    (is (= ((rule :a {:text-decoration "none"}))
           [:a {:text-decoration "none"}]))
    (is (thrown? ExceptionInfo (rule 1)))))

#_(deftest i-fail
  (is false "I always fail"))

#_(deftest i-pass
  (is true "I will NEVER fail you!"))
