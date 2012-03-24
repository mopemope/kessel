(ns kessel
  (:use [clojure.algo.monads]))

(def parser-m (state-t maybe-m))

(defn return [v]
  (with-monad parser-m
    (m-result v)))

(defn >>= [p f]
  (with-monad parser-m
    (m-bind p f)))

(defn >>== [p f]
  (>>= p #(return (f %))))

(defn <$> [f p]
  (>>= p #(return (f %))))

(defn >> [p1 p2]
  (>>= p1 (fn [_] p2)))

(defn either [& parsers]
  (apply (:m-plus parser-m) parsers))

(def <|> either)

(defmacro let-bind
  "Wraps body in `domonad' boilerplate"
  [& body]
  `(domonad parser-m
            ~@body))

(defn any-token [^String strn]
  (if (= "" strn)
    nil
    [(first strn) (.substring strn 1)]))

(defn eof [strn]
  (if (= "" strn)
    ["" ""]
    nil))

(defn nothing [strn]
  ["" strn])

(defn satisfy [pred]
  (domonad parser-m
           [c any-token :when (pred c)]
    c))

(defn is-char [c]
  (satisfy (partial = c)))

(defn not-char [c]
  (satisfy (partial (comp not =) c)))

(defn optional [p]
  (either p nothing))

(defn option [default p]
  (either p default))

(defn string [target]
  (>>==
   (with-monad parser-m
     (m-seq (map is-char target)))
   #(apply str %)))

(declare many1)

(defn many [parser]
  (optional (many1 parser)))

(defn many1 [parser]
  (let-bind [a parser
             as (many parser)]
            (concat [a] as)))

(defn end-by-m [f p sep]
  (f (let-bind [r p
                _ sep]
               r)))

(defn end-by [p sep]
  (end-by-m many p sep))

(defn end-by-1 [p sep]
  (end-by-m many1 p sep))


(defn sep-by-1 [p sep]
  (let-bind [x p
             xs (many (>> sep p))]
            (cons x xs)))

(defn sep-by [p sep]
  (either (sep-by-1 p sep) (return ())))

(defn followed-by [p sep]
  (let-bind [r p
             _ sep]
            r))

(defn interpose2 [sep ps]
  (with-monad parser-m
    (m-reduce conj [] (clojure.core/interpose sep ps))))

(def letter
     (satisfy #(Character/isLetter ^Character %)))

(def upper-char
     (satisfy #(Character/isUpperCase ^Character %)))

(def lower-char
     (satisfy #(Character/isLowerCase ^Character %)))

(def digit
     (satisfy #(Character/isDigit ^Character %)))

(defn one-of [target-strn]
  (let [chs (into #{} target-strn)]
    (satisfy #(contains? chs %))))

(defn none-of [exclusion-strn]
    (let [str-chars (into #{} exclusion-strn)]
          (satisfy #(not (contains? str-chars %)))))

(def aspace (satisfy #(= % \space)))

(def space (one-of " \r\n\t"))

(def spaces (many space))

(defn lexeme [p]
  (let-bind [a p _ spaces] a))

(defn symb [name]
  (lexeme (string name)))

(def semi (symb ";"))
(def colon (symb ":"))
(def comma (symb ","))

;; Convert the result of a parse to a string, if it's a list then concatenates the list...
(defn stringify [p]
  (>>= p #(return (if (seq? %) (apply str %) (str %)))))

(def base-identifier
  (let-bind [c  letter
             cs (many (either letter digit))]
   (apply str (cons c cs))))

(def identifier
     (lexeme base-identifier))

(def integer
     (lexeme (>>== (stringify (many1 digit))
                   #(new Integer ^String %))))

(defn between [open close p]
  (let-bind [_ open
             x p
             _ close]
            x))

(defn parens [p]
  (between (symb "(") (symb ")") p))

(defn brackets [p]
  (between (symb "[") (symb "]") p))

(defn braces [p]
  (between (symb "{") (symb "}") p))

(def string-literal
  (stringify (lexeme (between (is-char \") (is-char \") (many (not-char \"))))))

(def eol
  (>> (optional (satisfy #(= % \return))) (satisfy #(= % \newline))))

(defn parse [parser input]
  (parser input))
