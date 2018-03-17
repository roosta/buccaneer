(ns scrappy-front.events
  (:require [scrappy-front.db :as db]
            ;; [ajax.core :as ajax]
            [reagent.debug :refer [log]]
            [clojure.string :as str]
            [cljs.core :as cljs]
            [clojure.walk :refer [postwalk]]
            [cljs.nodejs :as nodejs]
            [re-frame.core
             :refer [reg-fx
                     dispatch
                     reg-event-db
                     reg-sub
                     reg-event-fx
                     reg-cofx]
             :as rf]))

(def file-types #"(?i)^.*\.(mkv|avi)$")
(def path (nodejs/require "path"))
(def fs (nodejs/require "fs"))

(def root-path "/home/roosta/netmedia/files")

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

#_(defn find-files
  [paths result]
  (if (seq paths)
    (let [files (read-dir (first paths))
          f (filter file? files)
          d (filter directory? files)]
      (recur (into (rest paths) d)
             (into result f)))
    result))

(defn fs-effect
  [{:keys [path on-success on-failure]}]
  (.readdir fs path (fn [err files]

                      ))
  )

(reg-fx :files fs-effect)

(reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-db
 :asd
 (fn [db]
   (.log js/console "hello")
   db))
