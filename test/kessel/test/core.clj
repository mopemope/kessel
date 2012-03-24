(ns kessel.test.core
  (:use [clojure.test])
  (:use [kessel]))

(deftest anything
  (is (= (any-token "Foo") [\F "oo"])
      "any-token rule takes first character")
  (is (= (any-token "") nil)
      "any-token rule returns nil when EOF"))

(deftest error
  (let [input ".   AAAA"
        r (parse (symb ".") input)]
    (println (get-rest-info input r))))

