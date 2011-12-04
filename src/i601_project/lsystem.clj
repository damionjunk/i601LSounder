(ns i601-project.lsystem
  (:use [i601-project.strings]
        [i601-project.utils]))

;; Method to generate a random L-System based on the
;; provided parameters.
;;
;; - alen The max axiom length
;; - rmap The key to binstring map of the desired alphabet
;; - prodkeys A string representing the keys we want to allow
;;            production rule generation for
;; - plen The max production rule length
(defn genr-lsystem
  "Returns a 'randomly' generated L-System."
  [{:keys [alen rmap prodkeys plen]}]
  (let [alphabet (vec (keys rmap))
        axiom (rand-bal-string alen (keys rmap))]
    {:v (apply str alphabet)
     :omega axiom
     :productions (reduce
                   (fn [agm k]
                     (assoc agm k (rand-bal-string plen alphabet)))
                   {}
                   prodkeys)
     }))

(defn lsys-to-bins
  "Returns a binary string version of the provided L-System map."
  [rmap {:keys [v omega productions]}]
  {:v (apply str (xlate-map v rmap))
   :omega (apply str (xlate-map omega rmap))
   :productions (reduce
                 (fn [agm [k v]]
                   (assoc agm (rmap k) (apply str (xlate-map v rmap))))
                 {}
                 productions)
   })
;;(lsys-to-bins revmap (genr-lsystem {:alen 5 :rmap revmap :prodkeys "NR+-<>" :plen 7}))

(defn bins-to-lsys
  "Returns our standard L-System from the provided binary string representation."
  [rmap clen {:keys [v omega productions]}]
  {:v (apply str (xlate-map (bchunked v clen) rmap))
   :omega (str-b-balance (apply str (xlate-map (bchunked omega clen) rmap)))
   :productions (reduce
                 (fn [agm [k v]]
                   (assoc agm (rmap k) (str-b-balance (apply str (xlate-map (bchunked v clen) rmap)))))
                 {}
                 productions)
   })

;; Runs the L-System generator. Uses reduce + (range generations)
;; to control the number of generations.
(defn lsys-run
  "Returns the results of running the L-System for the specified number of generations."
  [generations {:keys [v omega productions]}]
  (reduce
   (fn [st n] (mapcat productions st))
   omega
   (range generations)))

;;(lsys-run 3 {:v "NR" :omega "NR" :productions {\N "NN" \R "RR"}})
;;=> (\N \N \N \N \N \N \N \N \R \R \R \R \R \R \R \R)
;;(lsys-run 0 (genr-lsystem {:alen 5 :rmap revmap :prodkeys "NR+-<>" :plen 7}))
;;=> "-N"