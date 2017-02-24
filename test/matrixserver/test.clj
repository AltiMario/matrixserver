(ns matrixserver.test
  (:require [matrixserver.core :as core]
            [matrixserver.util :as ut])
  (:use midje.sweet))

(def m1 [[1 2] [4 5] [6 7]])
(def m2 [[11 12 13] [14 15 16]])
(core/initialize-atom!)

(facts "About get data from matrix"
       (fact "taking the right column data"
             (core/get-column m1 1) => [2 5 7]
             (core/get-column m1 7) => nil)
       (fact "taking the right row data"
             (core/get-row m2 0) => [11 12 13]
             (core/get-row m2 4) => nil))

(facts "About parallelize the calc"
       (fact "number of elems to parallelize"
             (reduce + (map #(count %) (core/dissects-and-distribute m1 m2))) => 9))
