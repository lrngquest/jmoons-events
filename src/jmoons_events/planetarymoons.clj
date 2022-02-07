(ns jmoons-events.planetarymoons
  (:require [jmoons-events.jupmoons :as jm] )
  (:require [jmoons-events.vsp  :as vp])
  (:require [jmoons-events.ut2tdb :as ut])
  (:require [jmoons-events.mthu  :as m] ) )

;; move test code from jupmoons for new file  2018Feb17

(defn get2t2ld4pos "values for one step in PlanetaryMoons" [timeJDU]
  (let [t0        (ut/UT_to_TDB (- timeJDU (/ 0.5 1440))) ;see sunmap.clj:253
        t1        (ut/UT_to_TDB (+ timeJDU (/ 0.5 1440)))
        lightDelay0  (vp/calcLightDelay 4 t0)
        lightDelay1  (vp/calcLightDelay 4 t1)
        jpos0     (vp/getEclipticPosition 4 (- t0 lightDelay0))
        jpos1     (vp/getEclipticPosition 4 (- t1 lightDelay1))
        jSunPos0  (vp/getHeliocentricPosition 4 (- t0 lightDelay0))
        jSunPos1  (vp/getHeliocentricPosition 4 (- t1 lightDelay1))  ]
    {:t0 t0  :t1 t1   :lightDelay0 lightDelay0  :lightDelay1 lightDelay1
     :jpos0 jpos0  :jpos1 jpos1   :jSunPos0 jSunPos0  :jSunPos1 jSunPos1} )
  )


(defn shapePM [mapXt0t1]
  (let [{:keys [t0 t1  lightDelay0 lightDelay1  jpos0 jpos1 jSunPos0 jSunPos1]}
        mapXt0t1
        pos0     (jm/fourMoonsValuesV jpos0    t0 lightDelay0)
        pos1     (jm/fourMoonsValuesV jpos1    t1 lightDelay1)
        sunPos0  (jm/fourMoonsValuesV jSunPos0 t0 lightDelay0)
        sunPos1  (jm/fourMoonsValuesV jSunPos1 t1 lightDelay1)  ]

    ;;reshape  above for desired access pattern of "PlanetaryMoons"
    [ [ (pos0 0) (pos1 0) (sunPos0 0) (sunPos1 0) ] ;; Io  2pos @ 2times each
      [ (pos0 1) (pos1 1) (sunPos0 1) (sunPos1 1) ] ;; Europa
      [ (pos0 2) (pos1 2) (sunPos0 2) (sunPos1 2) ] ;; Ganymede
      [ (pos0 3) (pos1 3) (sunPos0 3) (sunPos1 3) ]  ]  ) ;; Callisto
  )


(defn testPM "event msg logic of PlanetaryMoons" [ p01s01 t0 ev0]
  (let [[pos0 pos1 sunPos0 sunPos1]  p01s01
        tr0  (:inFront pos0)       tr1  (:inFront pos1)
        oc0  (:behind  pos0)       oc1  (:behind  pos1)
        ec0  (:behind  sunPos0)    ec1  (:behind  sunPos1)
        sh0  (:inFront sunPos0)    sh1  (:inFront sunPos1)
        mnNum  (:num pos0)
        prTm   (m/getTimeHMS t0)
        ev1 (cond
              (and (not tr0) tr1)  (conj ev0 (str prTm mnNum ". Tr.I"))
              (and tr0 (not tr1))  (conj ev0 (str prTm mnNum ". Tr.E"))
              (and (not oc0) oc1 (not ec0)) (conj ev0 (str prTm mnNum ". Oc.D"))
              (and oc0 (not oc1) (not ec1)) (conj ev0 (str prTm mnNum ". Oc.R"))
              (and (not ec0) ec1 (not oc0)) (conj ev0 (str prTm mnNum ". Ec.D"))
              (and ec0 (not ec1) (not oc1)) (conj ev0 (str prTm mnNum ". Ec.R"))
              :else                         ev0 )
        ev2 (cond (and (not sh0) sh1)  (conj ev1 (str prTm mnNum ". Sh.I"))
                  (and sh0 (not sh1))  (conj ev1 (str prTm mnNum ". Sh.E"))
                  :else                ev1 )    ]
    ev2)
  )


(def vmaxm {"Io"0.0147 "Europa"0.0117 "Ganymede"0.0092 "Callisto"0.0070} )

(defn calcDeltaT [ mpos]
  (let [vmax    (vmaxm (:name mpos))
        d       (:d mpos)
        d2      (if (> d 1.0)  (- d 1.0)  (- 1.0 d))
        deltaT  (max (m/ifloor (* (/ d2 vmax) 0.75)) 1) ]
    deltaT) )


(defn getSearchDeltaT "" [msP]
  (let [iom  (apply min (map calcDeltaT (msP 0) ) )
        eum  (apply min (map calcDeltaT (msP 1) ) )
        gnm  (apply min (map calcDeltaT (msP 2) ) )
        clm  (apply min (map calcDeltaT (msP 3) ) )
        all  [120 iom eum gnm clm]
        sdt  (apply min all)  ]
;    (println "t0 "(:t0 mapxt0t1)  "  searchDeltaT "sdt )
    sdt)
  )


(defn printPMevents "" [ t vvsPM]
  (let [ioev  (testPM (vvsPM 0) t [])
        euev  (testPM (vvsPM 1) t [])
        gnev  (testPM (vvsPM 2) t [])
        clev  (testPM (vvsPM 3) t [])
        _     (when (> (count ioev) 0)  (println ioev)) ;; _ ==> ignore value
        _     (when (> (count euev) 0)  (println euev))
        _     (when (> (count gnev) 0)  (println gnev))
        _     (when (> (count clev) 0)  (println clev))      ]  )
  )


(defn getEventsForOneMinuteSpan "" [ t]
  (let [mapXt0t1  (get2t2ld4pos t)
        vvsPM     (shapePM mapXt0t1)
        sDeltaT   (getSearchDeltaT vvsPM)  ]
    (printPMevents (:t0 mapXt0t1) vvsPM)
    sDeltaT) ;; return next searchDeltaT
  )
