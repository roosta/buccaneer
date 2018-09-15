(ns qmedia-front.fs
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
            [cljs.nodejs :as nodejs]))

;; Match files that does not include sample in filename, and have the file
;; extension mkv or avi
(def file-types-re #"(?i)^(?!.*(sample)).*\.(mkv|avi)$")
(def path (nodejs/require "path"))
(def fs (nodejs/require "fs"))

(defn directory?
  [file]
  (-> (.lstatSync fs file)
      (.isDirectory)))

(defn file?
  [file]
  (let [stat (.lstatSync fs file)]
    (and (.isFile stat)
         (seq (re-seq file-types-re file)))))

(defn read-dir
  [dir]
  (map #(.join path dir %) (.readdirSync fs dir)))

(defn file-seq
  [dir]
  (tree-seq
   (fn [f] (directory? f))
   (fn [d] (read-dir d))
   dir))

(defn files
  [dir]
  (let [filtered (filter file? (file-seq dir))]
    (map (fn [file]
           (let [ext (str (first (re-seq #"\.[0-9a-z]+$" file)))]
             {:full file
              :dirname (.dirname path file)
              :extension ext
              :basename (.basename path file ext)}))
         filtered)))

(defn effect
  [{:keys [dir on-success on-failure]}]
  (if (.existsSync fs dir)
    (dispatch (conj on-success (files dir)))
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
