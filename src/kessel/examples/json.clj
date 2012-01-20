(ns kessel.examples.json
  ^{:doc "This example uses kessel to create a simplified JSON parser"}
  (:use [kessel]))

(def jkey (lexeme (either natural string-literal)))
(def jvalue []
  (doparser [p (lexeme (either jdict jlist natural string-literal))]
            p))

(defn jkey-value
  []
  (doparser [k jkey
             _ (lexeme colon)
             v jvalue]
            [k v]))

(defn jdict
  []
  (doparser [kvs (between \{ \} (sep-by jkey-value (lexeme comma)))]
            (reduce conj {} kvs)))

(def jlist (between \( \) (sep-by jvalue comma)))
