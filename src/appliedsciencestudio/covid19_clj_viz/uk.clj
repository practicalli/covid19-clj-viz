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


;; Start an Oz server on a specific port
;; using 8044 for UK (matching telephone country code)
;; Evaluating the server will open a web page waiting for a Spec (oz/view!)

(oz/start-server! 8044)


;; Minimum viable geographic visualization
;; Googling to find a workable GEO.json file for the UK
;; https://raw.githubusercontent.com/martinjc/UK-GeoJSON/master/json/administrative/eng/lad.json

(oz/view! {:data {:url    "/public/data/uk-england-lad.geo.json"
                  :format {:type     "json"
                           :property "features"}}
           :mark "geoshape"})
