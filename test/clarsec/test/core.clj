(ns clarsec.test.core
  (:use [clarsec :as c]))

(deftest anything
  (is (= (c/any-token "Foo") (c/consumed "F" "oo"))
      "any-token rule takes first character")
  (is (= (c/any-token "") nil)
      "any-token rule returns nil when EOF"))
