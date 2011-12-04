(ns i601-project.core
  (:use [i601-project.lsystem]
        [i601-project.music]
        [i601-project.turtle]))

;; REPL session
(defn play-it
  [s]
  (let [t (music-turtle :dur (/ 1 8) :bpm 89)]
    (println "Playing..." s)
    (doseq [x s]
      ((trans-map x) t))
    (tp (metro) (:notes-stack @t))))


(defn play-from-gen
  [& {:keys [pop pos ls-gens] :or {ls-gens 1 pos 0}}]
  (play-it (apply str (lsys-run ls-gens (nth pop pos)))))


;; Test the turtle without L-System
;;(play-it "NNN++NN++++NNN")
;; Polyphony Turtle bracketed test
;;(play-it ">>>>>>[NNN][<<<<<N++N++N++N][<<<<<+++N++N++N++N][----NNN]")