(ns jmoons-events.core
	(:require [jmoons-events.planetarymoons :as pm])
	(:require [jmoons-events.jupmoons :as jm] )
        (:require [jmoons-events.mthu :as m] )
  (:gen-class))


(defn getJD  "extr func"    [y m d h mn s]
  (let [Y (if (< m 3) (dec y) y)
        M (if (< m 3) (+ m 12) m)
        A (int (/ Y 100))
        B (+ (- 2 A)  (/ A 4))
        dayFraction (/ (+ h (/ (+ mn (/ s 60.0)) 60.0)) 24.0)
        jd  (+ dayFraction
               (int (* 365.25 (+ Y 4716)))
               (int (* 30.6001 (+ M 1)))
               d  B  -1524.5)
        ]
    jd)   ;;Assert jd not in 2299150.0..2299160.0 !
  )

(defn getYMD [t] ;;used
  (let [[y m d h mt s] (m/getDate t)]  (format "%4d-%02d-%02d " y m d)) )




(defn eventFinder "" [startJDU endJDU]
  (loop [s 0   t (/ (Math/floor (* startJDU 1440.0)) 1440.0) ]
    (if (>= t endJDU)
      s
      (recur  (inc s)  (+ t (/ (pm/getEventsForOneMinuteSpan t) 1440.0)) ) )
    ) )

(defn -main  "print Jupiters Moons events for interval"  [y m d]
  ;; if changed to display in local time, start at hour:6        ------v
  (let [jdst   (getJD (read-string y) (read-string m) (read-string d)  0 0 0)
        jden   (+ jdst 1.0)  ]
    (do  ;;      (println (m/getDate jdst))
      (println (getYMD jdst) " UT" )
      (println "s "(eventFinder jdst jden))
      ) )
  )
