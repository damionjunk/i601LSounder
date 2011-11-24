(ns i601-project.lsystem)

(defn pad
  [num len]
  (if (> len (.length (str num)))
    (pad (str 0 num) len)
    (str num)))

(defn bin-string
  "Generates a 0 padded binary string from the given Integer to fit.
  Padding matches the width set by the maximum Integer."
  [x max]
  (pad (Integer/toString x 2) (count (Integer/toString max 2))))

;; Swap keys and values.
(defn map-invert [m] (reduce #(assoc %1 (val %2) (key %2)) {} binmap))

(defn parse-int [s]
  (Integer. (re-find #"[0-9]*" s)))



;; 6 elements, 2 branching
;; 8 total.
;; 000 001 010 011 100 101 110 111
(def alphabet "NR+-<>[]")
(def binmap (zipmap (for [x (range 0 (count alphabet))] (bin-string x (dec (count alphabet)))) alphabet))
(def revmap (map-invert binmap))
;;revmap
;;{\N "000", \R "001", \+ "010", \- "011", \< "100", \> "101", \[ "110", \] "111"}
;;binmap
;;{"111" \], "110" \[, "101" \>, "100" \<, "011" \-, "010" \+, "001" \R, "000" \N}


(defn bins-vec
  [num-elems bin-map]
  ;; generate 'num-elems' elements from the binmap.
  ;; bracketing syntax may not be correct here.
  (reduce #(conj %1 (bin-string (parse-int (str %2)) (dec (count bin-map)))) []
          (loop [pos num-elems bstr ""]
            (if (pos? pos)
              (recur
               (dec pos)
               (str (rand-int (count bin-map)) bstr))
              bstr))
          ))
;;(bins-vec 5 binmap)
;;["001" "111" "000" "100" "100"]

(defn bins-seq [n binmap]
  (repeatedly n #(bin-string (rand-int (count binmap)) (dec (count binmap)))))
;;(bins-seq 5 binmap)
;;("001" "100" "110" "111" "101")


(defn n-bins-vecs
  "Provides an n element seq of binary string vectors."
  [n maxt binmap]
  (take n (repeatedly #(bins-vec (inc (rand-int maxt)) binmap))))

  ;;
  (n-bins-vecs 2 3 binmap)
;; (["100" "100" "010"] ["001" "101"])





(defn gen-lsys-genotype
  [olen maxp bsmap]
  (let [alphabet (reduce #(conj %1 (key %2)) [] bsmap)
        initial (repeatedly (inc (rand-int olen)) #(rand-nth (keys bsmap)))]
    {:v alphabet
     :omega (vec initial)
     :productions (reduce (fn [m k] (assoc m k (vec (bins-seq (inc (rand-int maxp)) bsmap)))) {} alphabet)
     }))

;; Maps from binstrings to production characters
;;
(defn gen-lsys-phenotype
  [geno bsmap]
  {:v (vec (for [x (geno :v)] (bsmap x)))
   :omega (vec (for [x (geno :omega)] (bsmap x)))
   :productions (reduce (fn [m [k v]] (assoc m (bsmap k) (for [x v] (bsmap x)))) {} (geno :productions))
   })

;;(gen-lsys-phenotype (gen-lsys-genotype 3 3 binmap) binmap)
;; {:v [\] \[ \> \< \- \+ \R \N],
;;  :omega [\[ \< \]],
;;  :productions {\] (\[ \<),
;;                \[ (\]),
;;                \> (\N \R),
;;                \< (\> \+ \>),
;;                \- (\N),
;;                \+ (\[ \[ \<),
;;                \R (\[), \N (\>)}}


;;(gen-lsys-genotype 3 3 binmap)
;; {:v ["111" "110" "101" "100" "011" "010" "001" "000"],
;;  :omega ["010" "110"],
;;  :productions {
;;      "000" ["110" "110"],
;;      "001" ["000" "011" "000"],
;;      "010" ["101" "101" "111"],
;;      "011" ["001" "001" "011"],
;;      "100" ["000" "011" "101"],
;;      "101" ["011"],
;;      "110" ["101" "000"],
;;      "111" ["011"] }}

(defn D0L-system
  ""
  [productions pos omega]
  (loop [d pos s omega]
    (if (zero? d)
      s
      (recur (dec d) (mapcat productions s)))))

