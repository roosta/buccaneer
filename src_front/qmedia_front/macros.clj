(ns qmedia-front.macros
  (:require [clojure-csv.core :as csv]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn load-tsv
  [filename]
  (as-> (io/resource (str "imdb/" filename)) x
    (slurp x)
    (csv/parse-csv x :delimiter \tab)))

(defn load-config
  []
  (edn/read-string
   (try
     (slurp "config.edn")
     (catch Exception e (println "There was an error reading config.edn, are you sure you provided one?")))))


(defmacro imdb-data
  [filename]
  (let [tsv (load-tsv filename)
        keys (mapv keyword (first tsv))
        data (map #(zipmap keys %) tsv)]
    `~data))

(defmacro env
  [kw]
  (let [config (load-config)
        value (kw config)]
    `~value))

(comment
  (imdb-data "title.basics.tsv"))
