(ns jmoons-events.mthu )

(defn rmod   "" [ x y]  (- x (* (Math/floor (/ x y)) y) )  )
(defn ifloor "" [x]     (int (Math/floor x)) )

(defn getDate
  " julian date to year,month,day,hour,minute,second UTC --  Meeus ch. 7"
  [jd ]
  (let [ Z (Math/floor (+ jd 0.5))
        F (- (+ jd 0.5) Z)
        A1 Z
        a (int (/ (- Z 1867216.25) 36524.25))
        A2 (+ A1 1 a  (/ a -4))
        A (if (>= Z 2299161)  A2  A1)

        B (+ A 1524)
        C (int (/ (- B 122.1) 365.25))
        D (int (* C 365.25))
        E (int (/ (- B D) 30.6001))
        exactDay    (- (- (+ F B) D) (int (* 30.6001 E)) )
        day (int exactDay)
        month (int ( if (< E 14) (- E 1) (- E 13)))
        year1 (- C 4715)
        year (if (> month 2)  (dec year1) year1)
        
        h (/ (* (- exactDay day) 86400) 3600.0) ; SECONDS_PER_DAY
        hour (int h)
        m (* (- h hour) 60.0)
        minute (int m)
        second (int (* (- m minute) 60.0) ) ]
      [year month day  hour minute second]
    )
  )

(defn getTimeHMS [t]
  (let [[y mn d h mt s] (getDate t)]    (format "%2d:%02d %2d" h mt s)  ))
