(ns matrixserver.system
  (:require [matrixserver.core :as core]
            [clojure.edn :as edn]
            [taoensso.timbre :as timbre]))


(defn -main []
  (let [matrices (edn/read-string (slurp "resources/matrices.edn"))]
    (core/initialize-atom!)
    (timbre/info "Process started.")
    (-> (core/dissects-and-distribute (:matrix-a matrices) (:matrix-b matrices))
        (core/aggregate-elaborations)
        (core/generate-output!)))
  (timbre/info "Process ended.")
  (System/exit 1))

(comment
  (-main))
