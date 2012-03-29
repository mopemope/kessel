(ns kessel.test.core
  (:use [clojure.test])
  (:use [kessel]))

(deftest string-test
  (let [input "Test"]
    (is (= ((string "Test") input) ["Test" ""]))))

(deftest re-test
  (let [input "Test"]
    (is (= ((regex "Test") input) ["Test" ""]))))

(deftest symb-test
  (let [input "Test    "]
    (is (= ((symb "Test") input) ["Test" ""]))))

(deftest anything
  (is (= (any-token "Foo") [\F "oo"])
      "any-token rule takes first character")
  (is (= (any-token "") nil)
      "any-token rule returns nil when EOF"))

; (def input "   A")

; (deftest error
  ; (println (parse$ (symb ".") input)))

; (time
  ; (dotimes [n 1000]
    ; (spaces input)))

; (time
  ; (dotimes [n 1000]
    ; (sp input)))

