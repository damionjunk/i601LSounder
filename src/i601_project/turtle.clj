(ns i601-project.turtle)

(def min-note-duration (/ 1 16))
(def max-note-duration (/ 2 1))
(def default-duration-mod (/ 1 8))

;; 
(defn dur-to-seconds
  ""
  [bpm dur]
  (let [m (* dur 240.0)]
    (/ m bpm)))

(defn music-turtle
  [& {:keys [note dur pos time bpm] :or {note 60 dur (/ 1 8) pos 0 time 0 bpm 120}}]
  (ref {:pos pos
        :note note
        :dur dur
        :time time
        :bpm bpm
        :stack []
        :notes-stack []}))

(defn push-state
  ""
  [turtle]
  (let [stack (:stack @turtle)
        state {:note (:note @turtle)
               :dur (:dur @turtle)
               :pos (:pos @turtle)
               :bpm (:bpm @turtle)
               :time (:time @turtle)}]
    (dosync (alter turtle merge {:stack (conj stack state)}))))

(defn pop-state
  ""
  [turtle]
  (let [stack (:stack @turtle)]
    (if-let [state (peek stack)]
      (dosync (alter turtle merge {:stack (pop stack)} state))
      @turtle)))

(defn note-fn
  ""  
  [f turtle notes]
  (let [note (:note @turtle) pos (:pos @turtle)]
    (if notes
      (dosync (alter turtle merge {:note (nth notes (mod (f pos) (count notes))) :pos (f pos)}))
      (dosync (alter turtle merge {:note (f note)})))))

(defn note-up
  ""
  ([turtle] (note-up turtle nil))
  ([turtle notes] (note-fn inc turtle notes)))

(defn note-down
  ""
  ([turtle] (note-down turtle nil))
  ([turtle notes] (note-fn dec turtle notes)))

(defn dur-fn
  ""
  [f turtle]
  (let [dur (:dur @turtle)]
    (dosync (alter turtle merge {:dur (f dur)}))))

(defn dur-up
  ""
  [turtle] (dur-fn #(min max-note-duration (+ default-duration-mod %)) turtle))

(defn dur-down
  ""
  [turtle] (dur-fn #(max min-note-duration (- % default-duration-mod)) turtle))

(defn t-note
  ""
  [turtle]
  (let [time (:time @turtle)
        t-up (dur-to-seconds (:bpm @turtle) (:dur @turtle))
        notes (:notes-stack @turtle)
        notem {:note (:note @turtle)
               :dur-secs t-up
               :dur (:dur @turtle)
               :time (:time @turtle)}]
    (dosync (alter turtle merge {:time (+ time t-up) :notes-stack (conj notes notem)}))))

(defn t-rest
  ""
  [turtle]
  (let [time (:time @turtle) dur (:dur @turtle) bpm (:bpm @turtle)]
    (dosync (alter turtle merge {:time (+ time (dur-to-seconds bpm dur))}))))

;;
;;

(def trans-map
  {\N t-note
   \R t-rest
   \+ note-up
   \- note-down
   \> dur-up
   \< dur-down
   \[ push-state
   \] pop-state
   })