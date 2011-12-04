(ns i601-project.evalgo
  (:require [incanter.distributions :as dist])
  (:use [i601-project.lsystem]
        [i601-project.strings]
        [i601-project.utils]))

;; The idea in this simple EA selective breeding system is:
;; 1. Generate an initial random population
;; 2. Select two parents (subjective fitness function)
;; 3. Generate Offspring w/Mutation,Crossover

(defn gen-pop
  [p ls-params]
  (take p (repeatedly #(genr-lsystem ls-params))))


;; We're defining a simple string cross-over function
;; that selects a crossover point at a valid point given
;; a binary string 'atom' length.
;;
;; Refactor into Better crossover outcomes.
(defn crossover
  [s1 s2 clen]
  (let [idx (* clen (quot (rand-int (min (count s1) (count s2))) clen))]
    (apply str (concat (take idx s1) (drop idx s2)))))
;;(crossover "100100100" "001001001" 3)

(defn seq-r-b-flip
  [seq m-fn clen]
  (let [s (apply str seq) c (count s)]
    (apply str (update-in (vec seq) [(rand-int c)] m-fn))))
;;(seq-r-b-flip "000000000" #(char (+ (quot 1 (- (inc (int %)) 48)) 48))  3)
;;=> ("000" "010" "000")

(defn mutate
  [seq eap]
  (let [mutations ((:mutations eap)) clen (:bs-len eap)]
    (nth (take (inc mutations)
               (iterate #(seq-r-b-flip % (eap :m-fn) clen) seq))
         mutations)))

;;(mutate "000000000" eap)
;;=> "111000000"


;; Given a mother and father, produce a set of offspring.
;;
(defn make-baby
  [m f eap]
  (let [axiom (crossover (:omega m) (:omega f) (:bs-len eap))]
    {:v (:v m)
     :omega (mutate axiom eap)
     :productions (reduce (fn [mp k]
                            (assoc mp k (mutate (crossover ((:productions m) k) ((:productions f) k) (:bs-len eap)) eap)))
                          {}
                          (keys (:productions m)))}))

;; Breed our organisms producing a specifiable number of offspring.
;; Produces binary string l-systems.
(defn breed-to-bin
  "Returns a sequence of n children bred from the mother and father."
  [m f nchildren eap]
  (repeatedly nchildren #(make-baby m f eap)))

(defn breed-lsystems
  [m f nchildren eap]
  (map #(bins-to-lsys (:bmap eap) (:bs-len eap) %) (breed-to-bin m f nchildren eap)))