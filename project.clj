(defproject jmoons-events "0.2.0"
  :description "Galilean Moons of Jupiter"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :main ^:skip-aot jmoons-events.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
