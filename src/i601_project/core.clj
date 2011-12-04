(ns i601-project.core
  (:require [incanter.distributions :as dist])
  (:use [i601-project.evalgo]
        [i601-project.lsystem]
        [i601-project.strings]
        [i601-project.music]
        [i601-project.turtle]
        [overtone.live :only (stop)]))

;; REPL session
(defn play-it
  [s]
  (let [t (music-turtle :dur (/ 1 8) :bpm 89)]
    (println "Playing..." s)
    (doseq [x s]
      ((trans-map x) t))
    (tp (metro) (:notes-stack @t))))

;; Test the turtle without L-System
(play-it "NNN++NN++++NNN")
;; Polyphony Turtle bracketed test
(play-it ">>>>>>[NNN][<<<<<N++N++N++N][<<<<<+++N++N++N++N][----NNN]")


;; REPL / Evo-Algs
(def eap {:bs-len 3
          :rmap revmap
          :bmap binmap
          :mutations #(dist/roulette-wheel [0.90 0.08 0.01 0.01])
          :m-fn #(char (+ (quot 1 (- (inc (int %)) 48)) 48))})

(def koch {:v "N+-"
           :omega "N++N++N"
           :productions {
                         \N "N-N++N-N"
                         }})

(def tweedle-boop {:v "RN+-"
           :omega "NRNNRR"
           :productions {
                         \N "N++[-----------N]N"
                         \R "RR[R+++NRN]--"
                         }})

(def tocker {:v "RN+-"
           :omega "++N--"
           :productions {
                         \N "R[+++++N]NN[-----R]R"
                         \R "[-----NR+++N]"
                         }})

(play-it (apply str (lsys-run 3 koch)))
(play-it (apply str (lsys-run 3 tweedle-boop)))
(play-it (apply str (lsys-run 3 tocker)))
(stop)

(defn breed
  ""
  [m f nc eap]
  (for [x (breed-lsystems (lsys-to-bins revmap m) (lsys-to-bins revmap f) nc eap)]
    x))

(def gen1 (breed tocker tweedle-boop 9 eap))

(defn play-from-gen
  [& {:keys [pop pos ls-gens] :or {ls-gens 1 pos 0}}]
  (play-it (apply str (lsys-run ls-gens (nth pop pos)))))

;; Play, altering Runlen and Child element
;; Find the ones you like
;; (stop)
(play-from-gen :pos 8 :pop gen1 :ls-gens 3)
(nth gen1 8)
(nth gen1 4)
;;{:v "RN+-", :omega "++NNRR", :productions {\R "[----++NRN]--", \N "R[+++++N]NN[---N]N"}}
;; Sounds great at 3 L-System Generations
;; "Digger"
;;{:v "RN+-", :omega "++N-RR", :productions {\R "[-----NR++]--", \N "R[+++++N]NN[---N]N"}}