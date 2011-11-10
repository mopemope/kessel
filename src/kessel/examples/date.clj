(ns kessel.examples.date
  (:use kessel)
  (:use [clojure.contrib.monads :only (domonad)]))

(defn titlecase [s]
  (let [s1 (.toUpperCase (.substring s 0 1))
        sr (.toLowerCase (.substring s 1))]
    (str s1 sr)))

(def separator (between (optional spaces) (optional spaces) (one-of "|/-")))

(def months ["january" "february" "march" "april"
             "may" "june" "july" "august"
             "september" "october" "november" "december"
             "jan" "feb" "mar" "apr" "may" "jun"
             "jul" "aug" "sep" "oct" "nov" "dec"])

(def month-day-counts [31 28 31 30 31 30 31 31 30 31 30 31])

(defn month-num [mon]
  (when-let [num (first (keep-indexed
                         #(if (= (.toLowerCase mon) %2) %1)
                         months))]
    (inc (rem num 12))))

(defn leap-year? [yr]
  (cond
   (= (rem yr 400) 0) true
   (= (rem yr 100) 0) false
   (= (rem yr 4) 0) true
   :else false))

(defn days-in-month [mo yr]
  (when-let [n (if (integer? mo) mo (month-num mo))]
    (if (and (= n 1) (leap-year? yr))
      29
      (month-day-counts (dec n)))))

(defn valid-date? [yr mo da]
  (let [dim (days-in-month mo yr)]
    (and (> da 0) (<= da dim))))

(def month-numeric
  (domonad parser-m
           [mon natural
            :when #(and (> mon 0) (< mon 13))]
           {:month-name (titlecase (months (dec mon)))
            :month mon}))

(def month-name
  (domonad parser-m
           [mon (stringify (many1 letter))
            :when #(first (filter (partial = (.toLowerCase mon))
                                  months))]
           {:month-name (titlecase mon)
            :month (month-num (.toLowerCase mon))}))

(def month (either month-numeric month-name))

(def year
  (domonad parser-m
           [yr natural]
           {:century (inc (int (/ yr 100)))
            :year yr}))

(def date
  (domonad parser-m
           [y year
            _ separator
            m month
            _ separator
            d natural :when (valid-date? (:year y) (:month m) d)]
           (merge y m {:day d})))









