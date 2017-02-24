(ns matrixserver.core
  (:require [matrixserver.util :as ut]
            [taoensso.timbre :as timbre])
  (:gen-class))


;;; MATRIX MANAGEMENT ;;;

(def final-matrix (atom nil))

(defn initialize-atom!
  "facility to reset the atom values during the REPL session and in unit test"
  []
  (reset! final-matrix {}))

(defn get-column
  "get the 'index' column from the matrix"
  [mx index]
  (try
    (into [] (map #(nth % index) mx))
    (catch Exception e (do
                         (timbre/error "Index ERROR")
                         nil))))

(defn get-row
  "get a 'index' row from the matrix"
  [mx index]
  (try
    (nth mx index)
    (catch Exception e (do
                         (timbre/error "Index ERROR")
                         nil))))

(defn parallelize-elaboration
  "a new thread is generated and uniquely identified, it calls the slave to do the elaboration r x c"
  [rowval columnval]
  (let [unique (keyword (ut/uuid))]
    (future
      (try
        (swap! final-matrix conj {unique (:result (ut/send-to-slave {:row    rowval
                                                                     :column columnval}))})
        (catch Exception e (timbre/error "Could not send the elaboration request id: " unique "\n" (.getMessage e))))
      :completed)
    unique))


(defn dissects-and-distribute
  "split the matrices into rows and columns and distribute the elaboration"
  [mxa mxb]
  (loop [newmx [] rowindex 0 extlimit (count (get-column mxa 0))]
    (if (> rowindex (- extlimit 1))
      newmx
      (recur (conj newmx (loop [newrow [] colindex 0 intlimit (count (get-row mxb 0))]
                           (if (> colindex (- intlimit 1))
                             newrow
                             (recur (conj newrow
                                          (parallelize-elaboration (get-row mxa rowindex) (get-column mxb colindex)))
                                    (inc colindex) intlimit)))) (inc rowindex) extlimit))))



(defn- check-data-availability
  "the elaboration can be un-complete when the data is required, this function will wait if some thread doesn't finish the job"
  [promisedmap]
  (loop [iter 1 limit 20]                                   ;it is the maximum iteration I want to wait
    (if (> iter limit)
      (do
        (println "too much waiting, something wrong happened")
        false)
      (do (if-not (= (reduce + (map #(count %) promisedmap)) (count @final-matrix))
            (do (if-not (nil? (vals @final-matrix))
                  (println iter "secs waited, partial results:" (vals @final-matrix)))
                (Thread/sleep 1000)                         ;wait for 1 sec the slowest thread
                (recur (inc iter) limit))
            true)))))


(defn aggregate-row
  "aggregate the data in a single row"
  [promisedrow]
  (into [] (map #(get @final-matrix %) promisedrow)))


(defn aggregate-elaborations
  "aggregate the data with the data received from the external elaboration when available"
  [promisedmap]
  (if (check-data-availability promisedmap)
    (into [] (map #(aggregate-row %) promisedmap))))


(defn generate-output!
  "print on console and in a file the output"
  [hmap]
  (println "Result:\n" (into [] (map #(str % "\n") hmap)))
  (spit "final-matrix.edn" hmap))

