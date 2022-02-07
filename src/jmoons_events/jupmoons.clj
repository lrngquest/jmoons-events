(ns jmoons-events.jupmoons
	(:require [jmoons-events.const :as cn] )
	(:require [jmoons-events.mthu  :as m] )
	)

;;  Lieske E5 theory ala JupitersMoons.java; refactored.

;; based on SkyviewCafe 4.0.36 (c) Kerry Shetline (2000-2007)

;; function complete (?) 2017Oct25

(defn sinDeg ""  [deg] (Math/sin (* deg (/ Math/PI 180.0))) )
(defn cosDeg ""  [deg] (Math/cos (* deg (/ Math/PI 180.0))) )
(defn atanDeg "" [deg] (/ (* (Math/atan deg) 180.0) Math/PI) )


(defn moonsInner
  "compute the first stage values for Galilean moons"
  [timeJDE lightDelay]
  (let [ t (- timeJDE 2443000.5 lightDelay)
        l1 (+ 106.07719  (* 203.488955790 t) )
        l2 (+ 175.73161  (* 101.374724735 t) )
        l3 (+ 120.55883  (*  50.317609207 t) )
        l4 (+  84.44459  (*  21.571071177 t) )
        p1 (+  97.0881  (* 0.16138586 t) )
        p2 (+ 154.8663  (* 0.04726307 t) )
        p3 (+ 188.1840  (* 0.00712734 t) )
        p4 (+ 335.2868  (* 0.00184000 t) )
        w1 (- 312.3346  (* 0.13279386 t))
        w2 (- 100.4411  (* 0.03263064 t) )
        w3 (- 119.1942  (* 0.00717703 t) )
        w4 (- 322.6186  (* 0.00175934 t) )
        GAMMA (+ (* 0.33033 (sinDeg (+ 163.679 (* 0.0010512 t))) )
                 (* 0.03439 (sinDeg (- 34.486  (* 0.0161713 t))) )   )
        PHI_l (+ 199.6766  (* 0.17379190 t) )
        psi (- 316.5182 (* 0.00000208 t))
        G  (+ 30.23756 (* 0.0830925701 t) GAMMA)
        G1 (+ 31.97853 (* 0.0334597339 t) )
        PIj 13.469942
      
      S (+ (*  0.47259 (sinDeg (* 2.0 (- l1  l2))) )
           (* -0.03478 (sinDeg (- p3 p4)) )
           (*  0.01081 (sinDeg (+ l2 (* -2.0 l3) p3)) )
           (*  0.00738 (sinDeg PHI_l) )
           (*  0.00713 (sinDeg (+ l2 (* -2.0 l3) p2)) )
           (* -0.00674 (sinDeg (+ p1 p3 (* -2.0 PIj) (* -2.0 G) )) )
           (*  0.00666 (sinDeg (+ l2 (* -2.0 l3) p4)) )
           (*  0.00445 (sinDeg (- l1 p3)) )
           (* -0.00354 (sinDeg (- l1 l2)) )
           (* -0.00317 (sinDeg (- (* 2.0 psi) (* 2.0 PIj))) )
           (*  0.00265 (sinDeg (- l1 p4)) )
           (* -0.00186 (sinDeg G) )
           (*  0.00162 (sinDeg (- p2 p3)) )
           (*  0.00158 (sinDeg (* 4.0 (- l1 l2))) )
           (* -0.00155 (sinDeg (- l1 l3)) )
           (* -0.00138 (sinDeg (+ psi w3 (* -2.0 PIj) (* -2.0 G))) )
           (* -0.00115 (sinDeg (* 2.0 (+ l1 (* -2.0 l2) w2))) )
           (*  0.00089 (sinDeg (- p2 p4)) )
           (*  0.00085 (sinDeg (+ l1 p3 (* -2.0 PIj) (* -2.0 G))) )
           (*  0.00083 (sinDeg (- w2 w3)) )
           (*  0.00053 (sinDeg (- psi w2)) )   )
        L (+ l1 S)
        B (atanDeg (+  (* 0.0006393 (sinDeg (- L w1)))
                       (* 0.0001825 (sinDeg (- L w2)))
                       (* 0.0000329 (sinDeg (- L w3)))
                       (* -0.0000311 (sinDeg (- L psi)))
                       (* 0.0000093 (sinDeg (- L w4)))
                       (* 0.0000075 (sinDeg (+ (* 3.0 L) (* -4.0 l2)
                                               (* -1.9927 S) w2) ) )
                       (* 0.0000046 (sinDeg (+ L psi  (* -2.0 PIj)
                                               (* -2.0 G)) ))    ))                       
        R (* 5.90569 (+ 1.0
                           (* -0.0041339 (cosDeg (* 2.0 (- l1 l2))))
                           (* -0.0000387 (cosDeg (- l1 p3)))
                           (* -0.0000214 (cosDeg (- l1 p4)))
                           (*  0.0000170 (cosDeg (- l1 l2)))
                           (* -0.0000131 (cosDeg (* 4.0 (- l1 l2))))
                           (*  0.0000106 (cosDeg (- l1 l3)))
                           (* -0.0000066 (cosDeg (+ l1 p3 (* -2.0 PIj)
                                                    (* -2.0 G))))   ))          
        K 17295.0
        X1   (* R (cosDeg (- L psi)) (cosDeg B))
        Y1   (* R (sinDeg (- L psi)) (cosDeg B))
        Z1   (* R (sinDeg B))  ; 1: Io

        S2 (+ (*  1.06476 (sinDeg (* 2.0 (- l2 l3))))
              (*  0.04256 (sinDeg (+ l1 (* -2.0 l2) p3)))
              (*  0.03581 (sinDeg (- l2 p3)))
              (*  0.02395 (sinDeg (+ l1 (* -2.0 l2) p4) ))
              (*  0.01984 (sinDeg (- l2 p4)))
              (* -0.01778 (sinDeg PHI_l))
              (*  0.01654 (sinDeg (- l2 p2)))
              (*  0.01334 (sinDeg (+ l2 (* -2.0 l3) p2) ))
              (*  0.01294 (sinDeg (- p3 p4)))
              (* -0.01142 (sinDeg (- l2 l3)))
              (* -0.01057 (sinDeg G))
              (* -0.00775 (sinDeg (* 2.0 (- psi PIj))))
              (*  0.00524 (sinDeg (* 2.0 (- l1 l2))))
              (* -0.00460 (sinDeg (- l1 l3)))
              (*  0.00316 (sinDeg (+ psi (* -2.0 G) w3 (* -2.0 PIj))))
              (* -0.00203 (sinDeg (+ p1 p3 (* -2.0 PIj) (* -2.0 G))))
              (*  0.00146 (sinDeg (- psi w3)))
              (* -0.00145 (sinDeg (* 2.0 G)))
              (*  0.00125 (sinDeg (- psi w4)))
              (* -0.00115 (sinDeg (+ l1 (* -2.0 l3) p3)))
              (* -0.00094 (sinDeg (* 2.0 (- l2 w2))))
              (*  0.00086 (sinDeg (* 2.0 (+ l1 (* -2.0 l2) w2))))
              (* -0.00086 (sinDeg (+ (* 5.0 G1) (* -2.0 G) 52.225)))
              (* -0.00078 (sinDeg (- l2 l4)))
              (* -0.00064 (sinDeg (+ (* 3.0 l3) (* -7.0 l4) (* 4.0 p4))))
              (*  0.00064 (sinDeg (- p1 p4)))
              (* -0.00063 (sinDeg (+ l1 (* -2.0 l3) p4)))
              (*  0.00058 (sinDeg (- w3 w4)))
              (*  0.00056 (sinDeg (* 2.0 (- (- psi PIj) G))))
              (*  0.00056 (sinDeg (* 2.0 (- l2 l4))))
              (*  0.00055 (sinDeg (* 2.0 (- l1 l3))))
              (*  0.00052 (sinDeg (+ (* 3.0 l3) (* -7.0 l4) p3 (* 3.0 p4))))
              (* -0.00043 (sinDeg (- l1 p3)))
              (*  0.00041 (sinDeg (* 5.0 (- l2 l3))))
              (*  0.00041 (sinDeg (- p4 PIj)))
              (*  0.00032 (sinDeg (- w2 w3)))
              (*  0.00032 (sinDeg (* 2.0 (- l3 G PIj))))    )
        L2 (+ l2 S2)
        B2  (atanDeg
             (+ (*  0.0081004 (sinDeg (- L2 w2)))
                (*  0.0004512 (sinDeg (- L2 w3)))
                (* -0.0003284 (sinDeg (- L2 psi)))
                (*  0.0001160 (sinDeg (- L2 w4)))
                (*  0.0000272 (sinDeg (+ l1 (* -2.0 l3) (* 1.0146 S2) w2)))
                (* -0.0000144 (sinDeg (- L2 w1)))
                (*  0.0000143 (sinDeg (+ L2 psi (* -2.0 PIj) (* -2.0 G))))
                (*  0.0000035 (sinDeg (+ (- L2 psi) G)))
                (* -0.0000028 (sinDeg (+ l1 (* -2.0 l3) (* 1.0146 S2) w3)))  ))
        R2  (* 9.39657  (+ 1.0
                           (*  0.0093848 (cosDeg (- l1 l2)))
                           (* -0.0003116 (cosDeg (- l2 p3)))
                           (* -0.0001744 (cosDeg (- l2 p4)))
                           (* -0.0001442 (cosDeg (- l2 p2)))
                           (*  0.0000553 (cosDeg (- l2 l3)))
                           (*  0.0000523 (cosDeg (- l1 l3)))
                           (* -0.0000290 (cosDeg (* 2.0 (- l1 l2))))
                           (*  0.0000164 (cosDeg (* 2.0 (- l2 w2))))
                           (*  0.0000107 (cosDeg (+ l1 (* -2.0 l3) p3)))
                           (* -0.0000102 (cosDeg (- l2 p1)))
                           (* -0.0000091 (cosDeg (* 2.0 (- l1 l3))))    ))
        K2 21819.0
        X2   (* R2 (cosDeg (- L2 psi)) (cosDeg B2))
        Y2   (* R2 (sinDeg (- L2 psi)) (cosDeg B2))
        Z2   (* R2 (sinDeg B2))  ; 2: Europa
        
        S3 (+ (*  0.16490 (sinDeg (- l3 p3)))
              (*  0.09081 (sinDeg (- l3 p4)))
              (* -0.06907 (sinDeg (- l2 l3)))
              (*  0.03784 (sinDeg (- p3 p4)))
              (*  0.01846 (sinDeg (* 2.0 (- l3 l4)))) ;;5
              (* -0.01340 (sinDeg G))
              (* -0.01014 (sinDeg (* 2.0 (- psi PIj))))
              (*  0.00704 (sinDeg (+ l2 (* -2.0 l3) p3)))
              (* -0.00620 (sinDeg (+ l2 (* -2.0 l3) p2)))
              (* -0.00541 (sinDeg (- l3 l4))) ;;10
              (*  0.00381 (sinDeg (+ l2 (* -2.0 l3) p4)))
              (*  0.00235 (sinDeg (- psi w3)))
              (*  0.00198 (sinDeg (- psi w4)))
              (*  0.00176 (sinDeg PHI_l))
              (*  0.00130 (sinDeg (* 3.0 (- l3 l4)))) ;;15
              (*  0.00125 (sinDeg (- l1 l3)))
              (* -0.00119 (sinDeg (+ (* 5.0 G1)  (* -2.0 G) 52.225)))
              (*  0.00109 (sinDeg (- l1 l2)))
              (* -0.00100 (sinDeg (+ (* 3.0 l3)  (* -7.0 l4) (* 4.0 p4))))
              (*  0.00091 (sinDeg (- w3 w4))) ;;20
              (*  0.00080 (sinDeg (+ (* 3.0 l3)  (* -7.0 l4) p3 (* 3.0 p4))))
              (* -0.00075 (sinDeg (+ (* 2.0 l2) (* -3.0 l3) p3)))
              (*  0.00072 (sinDeg (+ p1 p3 (* -2.0 PIj)  (* -2.0 G))))
              (*  0.00069 (sinDeg (- p4 PIj)))
              (* -0.00058 (sinDeg (+ (* 2.0 l3)  (* -3.0 l4) p4))) ;;25
              (* -0.00057 (sinDeg (+ l3 (* -2.0 l4) p4)))
              (*  0.00056 (sinDeg (+ l3 p3 (* -2.0 PIj) (* -2.0 G))))
              (* -0.00052 (sinDeg (+ l2 (* -2.0 l3) p1)))
              (* -0.00050 (sinDeg (- p2 p3)))
              (*  0.00048 (sinDeg (+ l3 (* -2.0 l4) p3))) ;;30
              (* -0.00045 (sinDeg (+ (* 2.0 l2) (* -3.0 l3) p4)))
              (* -0.00041 (sinDeg (- p2 p4)))
              (* -0.00038 (sinDeg (* 2.0 G)))
              (* -0.00037 (sinDeg (+ (- p3 p4)  (- w3 w4) )))
              (* -0.00032 (sinDeg (+ (* 3.0 l3) (* -7.0 l4) (* 2.0 p3) (* 2.0 p4)))) ;;35
              (*  0.00030 (sinDeg (* 4.0 (- l3 l4))))
              (*  0.00029 (sinDeg (+ l3 p4 (* -2.0 PIj) (* -2.0 G))))
              (* -0.00028 (sinDeg (+ w3 psi (* -2.0 PIj) (* -2.0 G))))
              (*  0.00026 (sinDeg (- l3 PIj G)))
              (*  0.00024 (sinDeg (+ l2 (* -3.0 l3) (* 2.0 l4)))) ;;40
              (*  0.00021 (sinDeg (* 2.0 (- l3 PIj G))))
              (* -0.00021 (sinDeg (- l3 p2)))
              (*  0.00017 (sinDeg (* 2.0 (- l3 p3))))    )
        L3 (+ l3 S3)
        B3 (atanDeg
            (+ (*  0.0032402 (sinDeg (- L3 w3)))
               (* -0.0016911 (sinDeg (- L3 psi)))
               (*  0.0006847 (sinDeg (- L3 w4)))
               (* -0.0002797 (sinDeg (- L3 w2)))
               (*  0.0000321 (sinDeg (+ L3 psi (* -2.0 PIj) (* -2.0 G))))
               (*  0.0000051 (sinDeg (+ (- L3 psi) G)))
               (* -0.0000045 (sinDeg (- (- L3 psi) G)))
               (* -0.0000045 (sinDeg (+ L3  psi (* -2.0 PIj))))
               (*  0.0000037 (sinDeg (+ L3  psi (* -2.0 PIj) (* -3.0 G))))
               (*  0.0000030 (sinDeg (+ (* 2.0 l2) (* -3.0 L3) (* 4.03 S3) w2)))
               (* -0.0000021 (sinDeg (+ (* 2.0 l2) (* -3.0 L3) (* 4.03 S3) w3)))
               ))
        R3 (* 14.98832
              (+ 1.0
                 (* -0.0014388 (cosDeg (- l3 p3)))
                 (* -0.0007919 (cosDeg (- l3 p4)))
                 (*  0.0006342 (cosDeg (- l2 l3)))
                 (* -0.0001761 (cosDeg (* 2.0 (- l3 l4))))
                 (*  0.0000294 (cosDeg (- l3 l4)))
                 (* -0.0000156 (cosDeg (* 3.0 (- l3 l4))))
                 (*  0.0000156 (cosDeg (- l1 l3)))
                 (* -0.0000153 (cosDeg (- l1 l2)))
                 (*  0.0000070 (cosDeg (+ (* 2.0 l2 ) (* -3.0 l3)  p3)))
                 (* -0.0000051 (cosDeg (+ l3 p3 (* -2.0 PIj) (* -2.0 G))))   ))
        K3 27558.0
        X3   (* R3 (cosDeg (- L3 psi)) (cosDeg B3))
        Y3   (* R3 (sinDeg (- L3 psi)) (cosDeg B3))
        Z3   (* R3 (sinDeg B3))  ; 3: Ganymede

        S4 (+ (*  0.84287 (sinDeg (- l4 p4)))
              (*  0.03431 (sinDeg (- p4 p3)))
              (* -0.03305 (sinDeg (* 2.0 (- psi PIj))))
              (* -0.03211 (sinDeg G))
              (* -0.01862 (sinDeg (- l4 p3))) ;; 5
              (*  0.01186 (sinDeg (- psi w4)))
              (*  0.00623 (sinDeg (+ l4 p4 (* -2.0 G) (* -2.0 PIj))))
              (*  0.00387 (sinDeg (* 2.0 (- l4 p4))))
              (* -0.00284 (sinDeg (+ (* 5.0 G1) (* -2.0 G) 52.225)))
              (* -0.00234 (sinDeg (* 2.0 (- psi p4)))) ;;10
              (* -0.00223 (sinDeg (- l3 l4)))
              (* -0.00208 (sinDeg (- l4 PIj)))
              (*  0.00178 (sinDeg (+ psi w4 (* -2.0 p4))))
              (*  0.00134 (sinDeg (- p4 PIj)))
              (*  0.00125 (sinDeg (* 2.0 (- l4 G PIj)))) ;;15
              (* -0.00117 (sinDeg (* 2.0 G)))
              (* -0.00112 (sinDeg (* 2.0 (- l3 l4))))
              (*  0.00107 (sinDeg (+ (* 3.0 l3) (* -7.0 l4) (* 4.0 p4))))
              (*  0.00102 (sinDeg (- l4 G PIj)))
              (*  0.00096 (sinDeg (- (* 2.0 l4) psi w4))) ;;20
              (*  0.00087 (sinDeg (* 2.0 (- psi w4))))
              (* -0.00085 (sinDeg (+ (* 3.0 l3) (* -7.0 l4) p3 (* 3.0 p4))))
              (*  0.00085 (sinDeg (+ l3 (* -2.0 l4) p4)))
              (* -0.00081 (sinDeg (* 2.0 (- l4 psi))))
              (*  0.00071 (sinDeg (+ l4 p4 (* -2.0 PIj) (* -3.0 G)))) ;;25
              (*  0.00061 (sinDeg (- l1 l4)))
              (* -0.00056 (sinDeg (- psi w3)))
              (* -0.00054 (sinDeg (+ l3 (* -2.0 l4) p3)))
              (*  0.00051 (sinDeg (- l2 l4)))
              (*  0.00042 (sinDeg (* 2.0 (- psi G PIj)))) ;;30
              (*  0.00039 (sinDeg (* 2.0 (- p4 w4))))
              (*  0.00036 (sinDeg (- (+ psi PIj) p4 w4)))
              (*  0.00035 (sinDeg (+ (- (* 2.0 G1) G) 188.37)))
              (* -0.00035 (sinDeg (+ (- l4 p4) (* 2.0 PIj) (* -2.0 psi))))
              (* -0.00032 (sinDeg (- (+ l4 p4) (* 2.0 PIj) G))) ;;35
              (*  0.00030 (sinDeg (+ (* 2.0 G1) (* -2.0 G) 149.15)))
              (*  0.00029 (sinDeg (+ (* 3.0 l3) (* -7.0 l4) (* 2.0 p3) (* 2.0 p4))))
              (*  0.00028 (sinDeg (+ (- l4 p4) (* 2.0 psi) (* -2.0 PIj))))
              (* -0.00028 (sinDeg (* 2.0 (- l4 w4))))
              (* -0.00027 (sinDeg (- p3 (+ p4 w3) w4))) ;;40
              (* -0.00026 (sinDeg (+ (* 5.0 G1) (* -3.0 G) 188.37)))
              (*  0.00025 (sinDeg (- w4 w3)))
              (* -0.00025 (sinDeg (+ l2 (* -3.0 l3) (* 2.0 l4))))
              (* -0.00023 (sinDeg (* 3.0 (- l3 l4))))
              (*  0.00021 (sinDeg (+ (* 2.0 l4) (* -2.0 PIj) (* -3.0 G)))) ;;45
              (* -0.00021 (sinDeg (+ (* 2.0 l3) (* -3.0 l4) p4)))
              (*  0.00019 (sinDeg (- l4 p4 G)))
              (* -0.00019 (sinDeg (- (* 2.0 l4) p3 p4)))
              (* -0.00018 (sinDeg (- l4 (+ p4 G))))
              (* -0.00016 (sinDeg (+ l4 p3 (* -2.0 PIj) (* -2.0 G)) ))    )
        L4 (+ l4 S4)
        B4 (atanDeg
            (+ (* -0.0076579 (sinDeg (- L4 psi)))
               (*  0.0044134 (sinDeg (- L4 w4 )))
               (* -0.0005112 (sinDeg (- L4 w3)))
               (*  0.0000773 (sinDeg (+ L4 psi (* -2.0 PIj) (* -2.0 G))))
               (*  0.0000104 (sinDeg (+ (- L4 psi) G))) ;; 5
               (* -0.0000102 (sinDeg (- (- L4 psi) G)))
               (*  0.0000088 (sinDeg (+ L4 psi (* -2.0 PIj) (* -3.0 G))))
               (* -0.0000038 (sinDeg (- (+ L4 psi (* -2.0 PIj)) G)))    ))        
        R4 (* 26.36273
              (+ 1.0
                 (* -0.0073546 (cosDeg (- l4 p4)))
                 (*  0.0001621 (cosDeg (- l4 p3)))
                 (*  0.0000974 (cosDeg (- l3 l4)))
                 (* -0.0000543 (cosDeg (+ l4 p4 (* -2.0 PIj) (* -2.0 G))))
                 (* -0.0000271 (cosDeg (* 2.0 (- l4 p4)))) ;; 5
                 (*  0.0000182 (cosDeg (- l4 PIj)))
                 (*  0.0000177 (cosDeg (* 2.0 (- l3 l4))))
                 (* -0.0000167 (cosDeg (- (* 2.0 l4) psi w4)))
                 (*  0.0000167 (cosDeg (- psi w4)))
                 (* -0.0000155 (cosDeg (* 2.0 (- l4 PIj G)))) ;;10
                 (*  0.0000142 (cosDeg (* 2.0 (- l4 psi))))
                 (*  0.0000105 (cosDeg (- l1 l4)))
                 (*  0.0000092 (cosDeg (- l2 l4)))
                 (* -0.0000089 (cosDeg (- l4 PIj G)))
                 (* -0.0000062 (cosDeg (+ l4 p4 (* -2.0 PIj) (* -3.0 G)))) ;;15
                 (*  0.0000048 (cosDeg (* 2.0 (- l4 w4))))    ))
        K4 36548.0
        X4   (* R4 (cosDeg (- L4 psi)) (cosDeg B4))
        Y4   (* R4 (sinDeg (- L4 psi)) (cosDeg B4))
        Z4   (* R4 (sinDeg B4))  ; 4: Callisto
        ]
;    (println "L " L  " B " B  " R " R)
    [;{:X X1   :Y Y1   :Z Z1   :R R    :K K         "Io"   "I"}
    ; {:X X2   :Y Y2   :Z Z2   :R R2   :K K2    "Europa"  "II"}
    ; {:X X3   :Y Y3   :Z Z3   :R R3   :K K3  "Ganymede" "III"}
    ; {:X X4   :Y Y4   :Z Z4   :R R4   :K K4  "Callisto"  "IV"}
     ]
    
    [{:X X1  :Y Y1  :Z Z1  :R R4  :K K4  :name      "Io" :num "  I"};compat.
     {:X X2  :Y Y2  :Z Z2  :R R4  :K K4  :name  "Europa" :num " II"}; version
     {:X X3  :Y Y3  :Z Z3  :R R4  :K K4 :name "Ganymede" :num "III"}; test!
     {:X X4  :Y Y4  :Z Z4  :R R4  :K K4  :name"Callisto" :num " IV"}  ]
    )

  )



(def elems
  [ [ 34.351519,   3036.3027748,    0.00022330,    0.000000037]
    [  5.202603209,   0.0000001913, 0.0,           0.0]
    [  0.04849793,    0.000163225, -0.0000004714, -0.00000000201]
    [  1.303267,     -0.0054965,    0.00000466,   -0.000000002]
    [100.464407,      1.0209774,    0.00040315,    0.000000404]
    [ 14.331207,      1.6126352,    0.00103042,   -0.000004464]  ])

(defn inner "" [PoT esrow]  (reduce + (map * PoT esrow)))

(defn getOrbitalElementsJupiter "JupitersMoons usecase only!" [timeJDE]
  (let [T      (/ (- timeJDE cn/JD_J2000)  36525.0)
        PoT    [1.0 T (* T T) (* T T T)] ; T**0 .. T**3
        elem   (vec (map (partial inner PoT) elems)) ]
;    (println "T:"T " PoT"PoT )
;    (println "elem:"elem)
    {:i (elem 3)  :OMEGA (m/rmod (elem 4) 360.0) } )
  )

(defn prep "" [ jpos  timeJDE lightDelay psi]
  (let [L0     (* (:longitude jpos) cn/R2Deg)
        B0     (* (:latitude  jpos) cn/R2Deg)
        DELTA  (:radius jpos)
        T0  (/ (- timeJDE 2433282.423) 36525.0)
        P   (+ (* 1.3966626 T0)  (* 0.0003088  T0 T0))
        T   (/ (- timeJDE 2415020.0  ) 36525.0)
        I   (+ 3.120262 (* 0.0006 T) )
        {:keys [OMEGA i]}   (getOrbitalElementsJupiter (- timeJDE lightDelay) )
        PHI    (- (+ psi P) OMEGA)    ]
    { :L0 L0  :B0 B0  :DELTA DELTA   :I I  :OMEGA OMEGA   :PHI PHI :i i})
  )

(defn sixRotations "" [ X Y Z  prepVals]
  (let [{:keys [L0 B0 DELTA I OMEGA PHI i]} prepVals
        
        A1  X   ;; Rotation towards Jupiter's orbital plane
        B1  (- (* Y (cosDeg I))  (* Z (sinDeg I)))
        C1  (+ (* Y (sinDeg I))  (* Z (cosDeg I)))

        A2  (- (* A1 (cosDeg PHI))  (* B1 (sinDeg PHI)))
        B2  (+ (* A1 (sinDeg PHI))  (* B1 (cosDeg PHI)))
        C2  C1  ;; Rotation towards ascending node of Jupiter's orbit

        A3  A2  ;; Rotation towards plane of ecliptic
        B3  (- (* B2 (cosDeg i))  (* C2 (sinDeg i)))
        C3  (+ (* B2 (sinDeg i))  (* C2 (cosDeg i)))
        
        A4  (- (* A3 (cosDeg OMEGA))  (* B3 (sinDeg OMEGA)))
        B4  (+ (* A3 (sinDeg OMEGA))  (* B3 (cosDeg OMEGA)))
        C4  C3  ;; Rotation towards the vernal equinox

        ;; Meeus does not explain these last two rotations, but they're
        ;; obviously related to accounting for the location of Jupiter.
        A5  (- (* A4 (sinDeg L0))  (* B4 (cosDeg L0)))
        B5  (+ (* A4 (cosDeg L0))  (* B4 (sinDeg L0)))
        C5  C4
        
        A6  A5
        B6  (+ (* C5 (sinDeg B0))  (* B5 (cosDeg B0)))
        C6  (- (* C5 (cosDeg B0))  (* B5 (sinDeg B0)))  ]
    [ A6 B6 C6] )
  )

(defn squared "" [p] (* p p))

(defn oneMoonValues "" [prepVals  D  inVals]
  (let [{:keys [X Y Z R K name num]}  inVals
        [A6 B6 C6]  (sixRotations X Y Z  prepVals)

        X2   (- (* A6 (Math/cos D)) (* C6 (Math/sin D)) )
        Y2   (+ (* A6 (Math/sin D)) (* C6 (Math/cos D)) )
        Z2   B6

        DELTA  (:DELTA prepVals)
        W    (/ DELTA (+ DELTA (/ Z2 2095.0)) )
        X3   (+ X2 (* (/ (Math/abs Z2) K)
                      (Math/sqrt (- 1.0 (squared (/ X2 R ))))) )

        Xm   (* X3 W)
        Ym   (* Y2 W)
        Zm   Z2
        inferior       (<= Zm 0.0)
        Y1             (* Ym 1.069303)  ;;const JUPITER_FLATTENING
        d              (Math/sqrt (+ (* Xm Xm) (* Y1 Y1)))
        withinDisc     (< d 1.0)
        inFrontOfDisc  (and withinDisc inferior)
        behindDisc     (and withinDisc (not inferior))
        ]
    { :x Xm :y Ym :z Zm :inferior inferior :within withinDisc
     :inFront inFrontOfDisc :behind behindDisc :d d :name name :num num})
  )


(defn fourMoonsValuesV "" [jpos timeJDE lightDelay]
  (let [t   (- timeJDE 2443000.5 lightDelay)
        psi (- 316.5182 (* 0.00000208 t)) ;dup in-line calc. see 'moonsInner'
        
        inVals      (moonsInner timeJDE lightDelay)

        prepVals    (prep jpos timeJDE  lightDelay psi)
        
        ;;compute D by rotations on "fictitious" moon
        [A6 B6 C6]  (sixRotations 0 0 1.0  prepVals)
        D           (Math/atan2 A6 C6)

        moonsVals   (vec (map (partial oneMoonValues prepVals D) inVals))   ]
    moonsVals)
  )

