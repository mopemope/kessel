(ns kessel.test.core
  (:use [clojure.test])
  (:use [kessel]))

(deftest anything
  (is (= (any-token "Foo") [\F "oo"])
      "any-token rule takes first character")
  (is (= (any-token "") nil)
      "any-token rule returns nil when EOF"))

(def input ".   A  ")

(deftest error
  (println (parse$ (symb ".") input)))

