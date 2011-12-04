(ns i601-project.session
  (:require [incanter.distributions :as dist])
  (:use [i601-project.core]
        [i601-project.strings]
        [i601-project.utils]
        [i601-project.evalgo]
        [i601-project.lsystem]
        [overtone.live :only (stop)]))

(def alphabet "NR+-<>[]")
(def binmap (zipmap (for [x (range 0 (count alphabet))] (bin-string x (dec (count alphabet)))) alphabet))
(def revmap (map-invert binmap))

(def eap {:bs-len 3
          :rmap revmap
          :bmap binmap
          :mutations #(dist/roulette-wheel [0.90 0.08 0.01 0.01])
          :m-fn #(char (+ (quot 1 (- (inc (int %)) 48)) 48))})

(def koch {:v "N+-" :omega "N++N++N"
           :productions {
                         \N "N-N++N-N"
                         }})

(def tweedle-boop {:v "RN+-[]" :omega "NRNNRR"
                   :productions {
                                 \N "N++[-----------N]N"
                                 \R "RR[R+++NRN]--"
                                 }})

(def tocker {:v "RN+-[]" :omega "++N--"
             :productions {
                           \N "R[+++++N]NN[-----R]R"
                           \R "[-----NR+++N]"
                           }})

(def cosmo {:v "RN+-<>[]" :omega ">N++N<--N"
              :productions {
                            \N "N++RN"
                            \R "RN---"
                            \> "N>>"
                            \< "R<<"
                            \+ "++"
                            \- "--"
                            }})
;;(play-it (apply str (lsys-run 2 cosmo)))


;; REPL / Evo-Algs
;;(play-it (apply str (lsys-run 3 koch)))
;;(play-it (apply str (lsys-run 3 tweedle-boop)))
;;(play-it (apply str (lsys-run 2 tocker)))
;;(stop)


;; =============================================================================
;; REPL Session for Live-Breeding L-Systems

;; Parent Generation using "Tocker" and "Tweedle-Boop"
(play-from-gen :pos 1 :pop [tocker tweedle-boop] :ls-gens 3)
;; Breed the two
(def gen1 (breed-lsystems tocker tweedle-boop 9 eap revmap))
;;
;; Experiment / Listen
(play-from-gen :pos 8 :pop gen1 :ls-gens 3)
(nth gen1 8)
(nth gen1 4)
;;
;; Breed #8 with Tocker -- Each you re-create gen1, you'll have
;; different results... Sometimes horrible ones!
(def gen2 (breed-lsystems tocker (nth gen1 5) 9 eap revmap))
(play-from-gen :pos 8 :pop gen2 :ls-gens 3)
(nth gen2 1)
(stop)

;; Some of the interesting L-Systems
;;
;; (H)  - Human created (or derived by trial/experimentation)
;;      - Tocker/Tweedle-Boop were derived from Koch Curve
;;
;; (EA) - Evolutionary Algorithm Bred 

;; (H) Tocker
;; {:omega "++N--", :productions {\N "R[+++++N]NN[-----R]R", \R "[-----NR+++N]"}, :v "RN+-"}

;; (H) Tweedle-Boop
;; {:omega "NRNNRR", :productions {\N "N++[-----------N]N", \R "RR[R+++NRN]--"}, :v "RN+-"}

;; (EA) "Digger"
;; {:v "RN+-", :omega "++N-RR", :productions {\R "[-----NR++]--", \N "R[+++++N]NN[---N]N"}}

;; (EA) Water-Tock
;; {:v "RN+-", :omega "++N-RR", :productions {\R "[-----NR+++N-]", \N "R[+++++N]NN[---N]N"}}