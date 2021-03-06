(ns appliedsciencestudio.covid19-clj-viz.johns-hopkins
  "Johns Hopkins COVID19 data sources, with util fns"
  (:require [appliedsciencestudio.covid19-clj-viz.world-bank :as world-bank]
            [clojure.data.csv :as csv])
  (:import [java.time LocalDate]
           [java.time.format DateTimeFormatter]))

(def covid19-confirmed-csv
  "From https://github.com/CSSEGISandData/COVID-19/tree/master/who_covid_19_situation_reports"
  (csv/read-csv (slurp "resources/COVID-19/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv")))

(def covid19-recovered-csv
  "From https://github.com/CSSEGISandData/COVID-19/tree/master/who_covid_19_situation_reports"
  (csv/read-csv (slurp "resources/COVID-19/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv")))

(def covid19-deaths-csv
  "From https://github.com/CSSEGISandData/COVID-19/tree/master/who_covid_19_situation_reports"
  (csv/read-csv (slurp "resources/COVID-19/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv")))

(def countries
  (set (map second covid19-confirmed-csv)))

(defn data-exists-for-country? [kind country]
  (let [rows (case kind
              :recovered covid19-recovered-csv
              :deaths    covid19-deaths-csv
              :confirmed covid19-confirmed-csv)]
   (some (comp #{country} second) rows)))

(defn parse-covid19-date [mm-dd-yy]
  (LocalDate/parse mm-dd-yy (DateTimeFormatter/ofPattern "M/d/yy")))

(defn country-totals
  "Aggregates given data for `country`, possibly spread across
  provinces/states, into a single sequence ('row')."
  [country rows]
  (->> rows
       rest
       (filter (comp #{country} second))
       (map #(drop 4 %))
       (map #(map (fn [n] (Integer/parseInt n)) %))
       (apply map +)))

(defn new-daily-cases-in [kind country]
  (assert (#{:recovered :deaths :confirmed} kind))
  (assert (data-exists-for-country? kind country))
  (let [rows (case kind
               :recovered covid19-recovered-csv
               :deaths    covid19-deaths-csv
               :confirmed covid19-confirmed-csv)]
    (->> rows
         (country-totals country)
         (partition 2 1)
         (reduce (fn [acc [n-yesterday n-today]]
                   (conj acc (max 0 (- n-today n-yesterday))))
                 [])
         (zipmap (map (comp str parse-covid19-date)
                      (drop 5 (first rows))))
         (sort-by val))))

(comment
  (country-totals "Germany" covid19-confirmed-csv)
  (country-totals "Australia" covid19-deaths-csv)
  
  (country-totals "Australia" covid19-confirmed-csv)
  (country-totals "Australia" covid19-deaths-csv)
  
  (new-daily-cases-in :confirmed "Germany")

  (new-daily-cases-in :confirmed "Korea, South")

  ;; test a country with province-level data
  (new-daily-cases-in :confirmed "Australia")
  
  )

(defn rate-as-of
  "Day-to-day COVID-19 case increase."
  [kind country days]
  (assert (#{:recovered :deaths :confirmed} kind))
  (let [country (get world-bank/normalize-country
                     country country)]
    (if (data-exists-for-country? kind country)
      (let [rows (case kind
                   :recovered covid19-recovered-csv
                   :deaths    covid19-deaths-csv
                   :confirmed covid19-confirmed-csv)
            row (partition 2 1 (country-totals country rows))
            [a b] (nth row (- (count row) (inc days)))]
        (if (zero? a)
          0
          (double (/ b a))))
      0)))

(comment

  (country-totals "Germany" covid19-deaths-csv)
  ;; (0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
  ;; 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 2 2 3 3 7 9 11 17 24)

  (country-totals "Germany" covid19-confirmed-csv)
  ;; (0 0 0 0 0 1 4 4 4 5 8 10 12 12 12 12 13 13 14 14 16 16 16 16 16
  ;; 16 16 16 16 16 16 16 16 16 17 27 46 48 79 130 159 196 262 482 670
  ;; 799 1040 1176 1457 1908 2078 3675 4585 5795 7272 9257)  
  
  (map (partial rate-as-of :confirmed "Germany")
       [1 2 3 5 10 20 30])
  ;; (1.272964796479648 1.254874892148404 1.263904034896401 1.768527430221367 1.30162703379224 1.703703703703704 1.0)
  
  )
