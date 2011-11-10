(ns kessel.test.core
  (:use [clojure.test])
  (:use [kessel :as c]))

(deftest anything
  (is (= (c/any-token "Foo") [\F "oo"])
      "any-token rule takes first character")
  (is (= (c/any-token "") nil)
      "any-token rule returns nil when EOF"))




