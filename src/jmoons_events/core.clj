;; Visualize Moons of Jupiter -- a Clojure learning exercise based on
;;  code from SkyviewCafe  4.0.36  (C) 2007 Kerry Shetline
;;   (see notice in e.g.  jupmoons.clj)
;;
;;  Swing code inspired by:
;;    https://github.com/stuarthalloway/programming-clojure/
;;        blob/master/src/examples/atom_snake.clj
;;  Notice from the pragprog.com book site:
;;  "  Source Code for Programming Clojure (2nd edition)
;;  Copyrights apply to this source code. You may use the source code in
;;  your own projects, however the source code may not be used to create
;;  training material, courses, books, articles, and the like. We make
;;  no guarantees that this source code is fit for any purpose."


(ns jmoons-events.core
  (:import 
    (javax.swing JFrame JPanel Timer)
    (java.awt Color Dimension  Font  FontMetrics)
    (java.awt.event ActionListener  MouseListener MouseEvent)
    (java.time Instant ZoneOffset OffsetDateTime)  ) ;; ==> Java 8 reqd.
  (:require [jmoons-events.planetarymoons :as pm])
  (:require [jmoons-events.jupmoons :as jm] )
  (:require [jmoons-events.vsp  :as vp])
  (:require [jmoons-events.ut2tdb :as ut])
  (:require [jmoons-events.mthu :as m] )
  (:gen-class) )


;; layer over the math models for Earth, Jupiter, Galilean moons
(defn get1t1ld2pos "values for one step in simulation" [timeJDU]
  (let [t0           (ut/UT_to_TDB timeJDU)
        lightDelay0  (vp/calcLightDelay 4 t0)
        jpos0        (vp/getEclipticPosition     4 (- t0 lightDelay0))
        jSunPos0     (vp/getHeliocentricPosition 4 (- t0 lightDelay0))  ]
    {:t0 t0   :lightDelay0 lightDelay0   :jpos0 jpos0   :jSunPos0 jSunPos0 } )
  )


(defn shapeS [mapXt0]
  (let [{:keys [t0  lightDelay0  jpos0  jSunPos0 ]}    mapXt0
        pos0     (jm/fourMoonsValuesV jpos0    t0 lightDelay0)
        sunPos0  (jm/fourMoonsValuesV jSunPos0 t0 lightDelay0)    ]

    ;;reshape  above for desired access pattern of simulation
    [ [ (pos0 0)  (sunPos0 0)  ]       ;; Io  2pos @ 1time
      [ (pos0 1)  (sunPos0 1)  ]       ;; Europa
      [ (pos0 2)  (sunPos0 2)  ]       ;; Ganymede
      [ (pos0 3)  (sunPos0 3)  ]  ]  ) ;; Callisto
  )


;; basics for display of Jupiter, moons, shadows
(def Dheight 150)   (def Dwidth 800) ;; arbitrary?
(def yctr  (/ Dheight 2))  (def xctr  (/ Dwidth 2))

(def discWidth  (/ Dwidth  28.0))  ;; Jupiter baseRadiusWidth
(def radius     (/ discWidth 2.0) )
(def discHeight (/ discWidth 1.069)) ;; Jupiter flattening
(def halfDiscHeight (/ discHeight 2.0) )

(def jupiterColor  (Color. 0xFFCC66))
(def label-font (Font. "SansSerif" Font/PLAIN 10) )
(def time-font  (Font. "SansSerif" Font/BOLD  12) )

(defn draw-shadow "" [^sun.java2d.SunGraphics2D g  vpos]
  (let [[pos sunpos] vpos
        ptx  (+ xctr (m/iround (* (:x sunpos) radius) ))
        pty  (- yctr (m/iround (* (:y sunpos) radius) )) ]
    (when (:inFront sunpos)
      (.setColor g Color/black)  (.fillRect g  (dec ptx) (dec pty) 3 3) )   ) )

(defn draw-moon-and-label "" [^sun.java2d.SunGraphics2D g  vpos]
  (let [[pos sunpos] vpos
        ^long ptx     (+ xctr (m/iround (* (:x pos) radius) ))
        ^long pty     (- yctr (m/iround (* (:y pos) radius) ))
        ml-color      (cond  (:behind  sunpos)  Color/blue     ;; eclipsed
                             (:inFront pos)     Color/gray     ;; transit
                             :else              Color/white )
        label-string  (subs (:name pos) 0 1)  ]  ;; one-letter label
     (when (not (:behind pos))     ;; i.e. not occluded
        (.setColor g ml-color )
        (.fillRect g (dec ptx) (dec pty) 3 3)
        (.setFont  g label-font) 
        (.drawString g label-string (+ ptx 4) (inc pty) ) )   )   )

        
;; add for interactive "controls"
(def sim-tse (atom {:t 0  :step (/ 10.0 1440)  :ena 1}))

(def tdltv [-1.0 (/ -60.0 1440) (/ -5.0 1440) (/ 5.0 1440) (/ 60.0 1440) 1.0])

(defn u-tdv "incr t"    [i] (assoc @sim-tse :t  (+ (tdltv i) (:t @sim-tse))) )
(defn u-ena "toggle ena" [] (assoc @sim-tse :ena (- 1 (:ena @sim-tse))))

(def btY  (+ Dheight 10))
(def keyinfo [
[[ 14 btY 34 24 ] "-1 Day"  ""  (partial u-tdv 0)  :white :med-grey ]
[[ 62 btY 34 24 ] "-1 Hr"   ""  (partial u-tdv 1)  :white :med-grey ]
[[110 btY 34 24 ] "-5 Min"  ""  (partial u-tdv 2)  :white :med-grey ]
[[158 btY 34 24 ] "+5 Min"  ""  (partial u-tdv 3)  :white :med-grey ]
[[206 btY 34 24 ] "+1 Hr"   ""  (partial u-tdv 4)  :white :med-grey ]
[[254 btY 35 24 ] "+1 Day"  ""  (partial u-tdv 5)  :white :med-grey ]
[[302 btY 68 24 ] "Run / Stop" "" u-ena            :white :med-grey ]    ])

(defn ffn "point-in-rect filter fn" [x y kv]
  (let [[rx ry wd ht]  (kv 0) ] ;;get the rect, de-structure
    (and (>= x rx) (< x (+ rx wd)) (>= y ry) (< y (+ ry ht))) )  )

(def colormap {:white  (Color. 0xFFFFFF)
               :med-grey (Color. 0x585858)  :dk-grey (Color. 0x383838) } )

(def f-fm (atom {:key-font 0  :kfm 0 }) )

(defn init "init font fontmetrics" [ ^JFrame jfrm]
  (let [kfnt  (Font. "Helvetica" Font/PLAIN  11) ]
    (swap! f-fm assoc :key-font kfnt  :kfm (.getFontMetrics jfrm kfnt) ) ))

(def WINDOW_HEIGHT (+ Dheight 10 (((keyinfo 0)0)3) 10 ))

(defn draw-string "" [ ^sun.java2d.SunGraphics2D g  ^String s
                      [rx ry wd ht] fg bg  ^FontMetrics fm]
  (let [descent      (.getMaxDescent fm)
        sheight      (.getHeight     fm)
        swidth       (.stringWidth   fm  s) ]
    (.setColor g (bg colormap) ) ;; was color[ fg]
    (.fillRect g rx ry wd ht)
    (.setColor g (fg colormap) )  ;; 'int' required below, else we get  ratio.
    (.drawString g s  (int(+ rx (/ (- wd swidth) 2)))
                 (int (+ ry (/ (- (* 2 ht) sheight) 2) descent)) )    )   )

(defn draw-btns "" [ ^sun.java2d.SunGraphics2D g]
  (.setColor g (:dk-grey colormap) )
  (.fillRect g 0 Dheight  Dwidth  (- WINDOW_HEIGHT Dheight) )
  (.setFont g (:key-font @f-fm))
  (doseq [i (range (count keyinfo))]
    (let [[rect lbl uu krsa fg bg]  (keyinfo i)]
      (draw-string g lbl rect fg bg (:kfm @f-fm)) )  )  )


;; gui "framework"
(defn viz-panel [frame ]
  (let [panel (proxy [ JPanel ActionListener   MouseListener] []
        
                (paintComponent [ ^sun.java2d.SunGraphics2D g]
                  (let [t               (:t @sim-tse)
                        vvpos           (->> (get1t1ld2pos t)   (shapeS ) )
                        [y mn d h m s]  (m/getDate t)    ]

                    (.setColor g Color/black)
                    (.fillRect g 0 0 Dwidth WINDOW_HEIGHT)
                    (draw-btns g)
                    
                    (.setColor g Color/white)     (.setFont g time-font)
                    (.drawString
                     g
                     (format "UTC %4d-%02d-%02d  %2d:%02d" y mn d h m)
                     (int(- Dwidth 200)) (int(- Dheight 10)))

                    (.setColor g jupiterColor)
                    (.fillOval g                    ;;draw planet
                               (- xctr (m/iround radius))
                               (- yctr (m/iround halfDiscHeight))
                               (m/iround discWidth)    (m/iround discHeight) )

                    (doseq [m (range (count vvpos))]
                      (draw-shadow         g (vvpos m)  )
                      (draw-moon-and-label g (vvpos m)  ) )
                    )  )
                
                (actionPerformed [e]  ;; timer tick
                  (let [{:keys [t step ena] }   @sim-tse ]
                    (when (= 1 ena) (swap! sim-tse assoc :t (+ t step)) ) )
                  (.repaint ^JPanel this) )

                (mouseClicked [^MouseEvent e]
                  (let [x  (.getX e)
                        y  (.getY e) 
                        kirow  (first (filter (partial ffn x y) keyinfo)) ]
                    (when kirow 
                      (reset! sim-tse ((kirow 3)))
                      (.repaint ^JPanel this) )  )     )
                (mousePressed [e] )  (mouseReleased[e] ) (mouseEntered [e] )
                (mouseExited  [e] )                
                ) ]
    (doto panel
      (.setPreferredSize (Dimension. Dwidth WINDOW_HEIGHT )) ) )  )

(def millis 100)

(defn vizfn [] 
  (let [frame           (JFrame. "Galilean Moons of Jupiter")
        _               (init  frame)
        ^JPanel panel   (viz-panel  frame )
        timer           (Timer. millis panel) ]
    (doto panel 
      (.setFocusable true)      
      (.addMouseListener panel )   )
    (doto frame 
      (.add panel)
      (.pack)
      (.setDefaultCloseOperation (JFrame/EXIT_ON_CLOSE))
      (.setVisible true))
    (.start timer)  )  ) 


(defn eventFinder "events text output" [startJDU endJDU]
  (reduce (fn [t v]
            (if (>= t endJDU)  (reduced v)
                (+ t (/ (pm/getEventsForOneMinuteSpan t) 1440.0))) 
             )  ;; --v t0                            ---v v0
          (/ (Math/floor (* startJDU 1440.0)) 1440.0)   (range  10000) )   )


(defn -main [ & args]
  (let [vra (vec (map read-string args)) ]    
    (case (count vra)
      0  (let [t        (.atOffset (Instant/now ) ZoneOffset/UTC)
               ty       (.getYear t)
               tm       (.getMonthValue t)
               td       (.getDayOfMonth t)  ]
           (swap! sim-tse assoc :t (m/get-jd ty tm td 0 0 0))  
           (vizfn )  )
      
      3 (do (swap! sim-tse assoc :t (m/get-jd (vra 0) (vra 1) (vra 2) 0 0 0))
            (vizfn ))
      
      4  (let [jdst  (m/get-jd (vra 0) (vra 1) (vra 2) 0 0 0 )
               jden  (+ jdst (vra 3))  ]  ;; days to print a table of events
           (println (m/getYMD jdst) " UT" )
           (println "s "(eventFinder jdst jden)) ))    )  )
