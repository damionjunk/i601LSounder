(ns i601-project.utils)

;; Swap the keys and values in the provided map.
(defn map-invert
  "Swaps the keys and values for the provided map."
  [m] (reduce #(assoc %1 (val %2) (key %2)) {} m))

;; Uses a regular expression to grab only the numeric
;; portion of the provided string, returning an Integer.
(defn parse-int
  "Converts strings to integers."
  [s] (Integer. (re-find #"[0-9]*" s)))

;; Pads the provided string with the given character.
(defn pad
  "Returns padded string with indicated character to indicated length."
  [s len & {:keys [pch] :or {pch "0"}}]
  (if (> len (.length s))
    (pad (str pch s) len :pch pch) s))