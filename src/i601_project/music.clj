(ns i601-project.music
  (:use [overtone.live]
        [overtone.inst synth]
        [overtone.inst drum]))

(def metro (metronome 89))

(definst tone [note 60 amp 0.3 dur 0.4]
  (let [snd (sin-osc (midicps note))
        env (env-gen (perc 0.01 dur) :action FREE)]
    (* env snd amp)))

(definst squarez [note 60 amp 0.3 dur 0.4 width 0.5]
  (* (env-gen (perc 0.2 dur) 1 1 0 1 FREE)
     (pulse (midicps note) width)))

(defn tp
  [tick stack]
  (for [sn stack]
    (at (metro (+ (:time sn) tick)) (tone (:note sn) 1 (:dur-secs sn)))))