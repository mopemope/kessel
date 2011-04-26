(ns clarsec.test.core
  (:use [clojure.test])
  (:use [clarsec :as c]))

(deftest anything
  (is (= (c/any-token "Foo") [\F "oo"])
      "any-token rule takes first character")
  (is (= (c/any-token "") nil)
      "any-token rule returns nil when EOF"))

