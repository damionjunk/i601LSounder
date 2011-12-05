(ns i601-project.strings
    (:use [i601-project.utils]))

(defn bin-string
  "Generates a 0 padded binary string from the given Integer to fit.
  Padding matches the width set by the maximum Integer."
  [x max]
  (pad (Integer/toString x 2) (count (Integer/toString max 2))))

;;  s - string to pull unbalanced chars from.
;; 
;;  :ls - left side, default '['
;;  :rs - right side, default ']'
(defn b-reduction
  "Reduces a string to un-balanced left and right enclosing characters."
  [s & {:keys [ls rs] :or {ls \[ rs \]}}]
  (reduce (fn [v ch]
            (if (= ch ls)
              (conj v ch)
              (if (= ch rs)
                (if (= ls (peek v))
                  (pop v)
                  (conj v ch))
                v)))
          []
          s))

;;(b-reduction "[stuff]")
;;=> []
;;(b-reduction "[[stuff]")
;;=> [\[]
;;(b-reduction "([[stuff])" :ls \( :rs \))
;;=> [\( \)]



(defn b-closers
  "Provides the 'brackets' necessary to close the given string."
  [s & {:keys [ls rs] :or {ls \[ rs \]}}]
  (for [x (b-reduction s)] (if (= ls x) rs ls)))
;; (b-closers "zz]abc][a aa[")
;; => (\[ \[ \] \])
;; (b-closers "[hi]")
;; => ()

(defn str-b-balance
  "Naive bracket balance in the provided string."
  [s & {:keys [ls rs] :or {ls \[ rs \]}}]
  (let [bc (b-closers s)]
    (reduce (fn [st ch]
              (if (= ls ch) (str ch st) (str st ch)))
            s
            bc)))
;;(str-b-balance "[close me")
;=> "[close me]"
;;(str-b-balance "[close me]][[")
;=> "[[close me]][[]]"
;;(str-b-balance "[already [valid]]")
;=> "[already [valid]]"


(defn b-balanced? [s]
  "Is the provided string bracket balanced?"
  (zero? (count (b-reduction s))))

;;(b-balanced? "[]?++[")
;; => false
;;(b-balanced? "[]?++[!!]")
;; => true


;;
;;
;; Random Strings

(defn rand-key-seq
  "Returns a random sequence of length n from the provided sequence."
  [n m] (repeatedly n #(rand-nth m)))
;;(rand-key-seq 5 (keys revmap))

(defn n-rand-seq
  "Provides an n element seq of random element seqs."
  [n maxt m]
  (take n (repeatedly #(rand-key-seq (inc (rand-int maxt)) m))))
;; (n-rand-seq 2 3 (keys binmap))
;;=> (["100" "100" "010"] ["001" "101"])
;; (n-rand-seq 2 3 (keys revmap))
;;=> ((\+ \-) (\>))

;; Some code to loop until we have a valid sequence.
;; (loop [s (rand-key-seq 5 (keys revmap))]
;;   (println s)
;;   (if (not (b-balanced? s))
;;     (recur (rand-key-seq 10 (keys revmap)))
;;     s))
;; (str-b-balance (apply str (rand-key-seq 10 (keys revmap))))



(defn rand-bal-string
  [rn kseq]
  (str-b-balance (apply str (rand-key-seq (inc (rand-int rn)) kseq))))
;;(rand-bal-string 5 (keys revmap))
;;=> "N-RR"


(defn n-rand-bal-strings
  [n rn kseq]
  (take n (repeatedly #(rand-bal-string rn kseq))))
;;(n-rand-bal-strings 5 5 (keys revmap))
;;=> ("N" "[]-" "R-" "<" "R>[<+]")

;; Chunks binary strings into a sequence of clen length strings.
(defn bchunked
  "Returns a sequence of the provided string chunked into pieces."
  [s l]
  (map #(apply str %) (partition l s)))

(defn xlate-map
  "Returns the translated sequence based on the sequence s and map m"
  [s m] (for [x s] (m x)))