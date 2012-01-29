(ns kessel.examples.json
  ^{:doc "This example uses kessel to create a simplified JSON parser

There are some complications because JSON's grammar is recursive (e.g.
Objects and Lists can contain themselves as values.)

Because of this, and the fact that Clojure does not have `letrec` as in
Scheme, one must create forward references via vars. `alter-root-var`,
at this time, is the only way I know how to do this, though there may
be other ways certainly."}
  (:refer-clojure :exclude [interpose])
  (:use [kessel :as k]))

(def jdict nil)
(def jlist nil)

(def jkey (k/lexeme (k/either k/natural k/string-literal)))

(def jvalue
  (k/doparser [p (k/lexeme (k/either #'jlist #'jdict k/natural k/string-literal))]
              p))

(def jkey-value
  (k/doparser [ky jkey
               _ (k/lexeme k/colon)
               v jvalue]
              [ky v]))

(alter-var-root #'jdict
                (fn [_]
                  (k/doparser [kvs (k/between (k/symb "{")
                                              (k/symb "}")
                                              (k/sep-by jkey-value
                                                        (k/lexeme k/comma)))]
                              (reduce conj {} kvs))))

(alter-var-root #'jlist
                (fn [_]
                  (k/between (k/symb "[")
                             (k/symb "]")
                             (k/sep-by jvalue k/comma))))

