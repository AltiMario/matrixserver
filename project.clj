(defproject matrixserver "1.0"
  :description "matrixserver"
  :main matrixserver.system
  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [com.taoensso/timbre "4.1.4"]
                 [cheshire "5.7.0"]
                 [clj-http "3.3.0"]]
  :profiles {:uberjar {:aot :all :uberjar-name "matrixserver.jar"}
             :dev     {:dependencies [[midje "1.8.3"]]
                       :plugins      [[lein-midje "3.2"]]}})
