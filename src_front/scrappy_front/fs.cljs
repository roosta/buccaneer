(ns scrappy-front.fs
  (:require [reagent.debug :refer [log]]
            [re-frame.core
             :refer [reg-fx
                     dispatch
                     reg-event-db
                     reg-sub
                     reg-event-fx
                     reg-cofx]
             :as rf]
            [clojure.string :as str]
            [cljs.nodejs :as nodejs]
            ))


(def file-types #"(?i)^.*\.(mkv|avi)$")

(def path (nodejs/require "path"))

(def fs (nodejs/require "fs"))


(defn directory?
  [file]
  (-> (.lstatSync fs file)
      (.isDirectory)))

(defn file?
  [file]
  (let [stat (.lstatSync fs file)]
    (and (.isFile stat) (not (nil? (re-matches file-types file))))))

(defn read-dir
  [dir]
  (map #(.join path dir %) (.readdirSync fs dir)))

(defn file-seq
  [dir]
  (tree-seq
   (fn [f] (directory? f))
   (fn [d] (read-dir d))
   dir))

(defn effect
  [{:keys [dir on-success on-failure]}]
  (if (.existsSync fs dir)
    (dispatch (conj on-success (filter file? (file-seq dir))))
    (dispatch (conj on-failure (str "Failed to read root directory: " dir)))))

#_(defn find-files
    [paths result]
    (if (seq paths)
      (let [files (read-dir (first paths))
            f (filter file? files)
            d (filter directory? files)]
        (recur (into (rest paths) d)
               (into result f)))
      result))
