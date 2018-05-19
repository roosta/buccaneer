(ns qmedia.macros
  (:require [clojure-csv.core :as csv]
            [clojure.java.io :as io]))

(defn load-tsv
  [filename]
  (as-> (io/resource (str "imdb/" filename)) x
    (slurp x)
    (csv/parse-csv x :delimiter \tab)))

(defmacro imdb-data
  [filename]
  (let [tsv (load-tsv filename)
        keys (mapv keyword (first tsv))
        data (map #(zipmap keys %) tsv)]
    `~data))

(comment
  (imdb-data "title.basics.tsv"))
