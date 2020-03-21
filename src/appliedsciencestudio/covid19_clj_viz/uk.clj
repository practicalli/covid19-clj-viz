;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Covid 19 - UK focus
;;
;; Using Johns Hopkins as a primary source of data on COVID19,
;; transforming and narrowing down information for UK
;; Creating Oz visualisation of specific regions of UK
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns appliedsciencestudio.covid19-clj-viz.viz
  (:require
   [appliedsciencestudio.covid19-clj-viz.johns-hopkins :as jh]
   [appliedsciencestudio.covid19-clj-viz.world-bank :as world-bank]
   [clojure.data.csv :as csv]
   [clojure.string :as string]
   [jsonista.core :as json]
   [oz.core :as oz]))
