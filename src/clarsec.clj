(ns clarsec
  (:use [clojure.contrib.monads]))

(def parser-m (state-t maybe-m))

(defn return [v]
  (with-monad parser-m
    (m-result v)))

(defn >>= [p f]
  (with-monad parser-m
    (m-bind p f)))

(defn >>== [p f]
  (>>= p #(return (f %))))

(defn >> [p1 p2]
  (>>= p1 (fn [_] p2)))

(defn either [& parsers]
  (apply (:m-plus parser-m) parsers))

(defn any-token [strn]
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

(defn string [strn]
   ((m-seq (map is-char strn)) strn))

(declare many1)

(defn many [parser]
  (optional (many1 parser)))

(defn many1 [parser]
  (domonad parser-m
           [a parser
            as (many parser)]
   (concat [a] as)))

(defn end-by-m [f p sep]
  (f (domonad parser-m
              [r p
               _ sep]
    r)))

(defn end-by [p sep]
  (end-by-m many p sep))

(defn end-by-1 [p sep]
  (end-by-m many1 p sep))

(defn sep-by-1 [p sep]
  (domonad parser-m
           [x p
            xs (many (>> sep p))]
    (cons x xs)))

(defn sep-by [p sep]
  (either (sep-by-1 p sep) (return ())))

(defn followed-by [p sep]
  (domonad parser-m
           [r p
            _ sep]
    r))

(def letter
     (satisfy #(Character/isLetter %)))

(def digit
     (satisfy #(Character/isDigit %)))

(defn one-of [target-strn]
  (let [chs (into #{} target-strn)]
    (satisfy #(contains? chs %))))

(def space (one-of " \r\n\t"))

(def spaces (many space))

(defn lexeme [p]
  (>> spaces p))

(defn symb [name]
  (lexeme (string name)))

;;(def semi (symb ";"))
;;(def comma (symb ","))

;; Convert the result of a parse to a string, if it's a list then concatenates the list...
(defn stringify [p]
  (>>= p #(return (if (seq? %) (apply str %) (str %)))))

(def base-identifier
  (domonad parser-m
           [c  letter
            cs (many (either letter digit))]
    (apply str (cons c cs))))

(def identifier
     (lexeme base-identifier))

(def natural
     (lexeme (>>== (stringify (many1 digit))
                   #(new Integer %))))

(defn between [open close p]
  (domonad parser-m
           [_ open
            x p
            _ close]
    x))

(defn parens [p]
  (between (symb "(") (symb ")") p))

(defn brackets [p]
  (between (symb "[") (symb "]") p))

(defn braces [p]
  (between (symb "{") (symb "}") p))


(def stringLiteral
     (stringify (lexeme (between (is-char \") (is-char \") (many (not-char \"))))))

(defn parse [parser input]
  (>>= (return input) (force parser)))

;;(defn -main []
;;  (println (parse (>> (delay letter) (delay letter)) "ca.")))