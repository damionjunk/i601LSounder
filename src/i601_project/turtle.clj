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
  (dosync  (let [stack (:stack @turtle)
                 state (select-keys @turtle [:note :dur :pos :bpm :time])]
             (alter turtle merge {:stack (conj stack state)}))))

(defn pop-state
  ""
  [turtle]
  (dosync
   (let [stack (:stack @turtle)]
     (if-let [state (peek stack)]
       (alter turtle merge {:stack (pop stack)} state)
       @turtle))))

(defn note-fn
  ""  
  [f turtle notes]
  (dosync
   (let [note (:note @turtle) pos (:pos @turtle)]
     (if notes
       (alter turtle merge {:note (nth notes (mod (f pos) (count notes))) :pos (f pos)})
       (alter turtle merge {:note (f note)})))))

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
  (dosync (let [dur (:dur @turtle)]
            (alter turtle merge {:dur (f dur)}))))

(defn dur-up
  ""
  [turtle] (dur-fn #(min max-note-duration (+ default-duration-mod %)) turtle))

(defn dur-down
  ""
  [turtle] (dur-fn #(max min-note-duration (- % default-duration-mod)) turtle))

(defn t-note
  ""
  [turtle]
  (dosync (let [time (:time @turtle)
                t-up (dur-to-seconds (:bpm @turtle) (:dur @turtle))
                notes (:notes-stack @turtle)
                notem (merge {:dur-secs t-up} (select-keys @turtle [:note :dur :time]))]
            (alter turtle merge {:time (+ time t-up) :notes-stack (conj notes notem)}))))

(defn t-rest
  ""
  [turtle]
  (dosync (let [time (:time @turtle) dur (:dur @turtle) bpm (:bpm @turtle)]
            (alter turtle merge {:time (+ time (dur-to-seconds bpm dur))}))))

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