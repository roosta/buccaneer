(ns qmedia-front.subs
  (:require
   [clojure.string :as str]
   [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
   [re-frame.core :refer [dispatch reg-event-db reg-sub reg-event-fx]]))


(reg-sub
 :root-dir
 (fn [db]
   (:root-dir db)))

(reg-sub
 :media
 (fn [db]
   (:media db)))

(reg-sub
 :themoviedb/config
 (fn [db]
   (:themoviedb/config db)))

(reg-sub
 :themoviedb.images/base-url
 :<- [:themoviedb/config]
 (fn [config]
   (-> config :images :base_url)))

(reg-sub
 :media.active/title
 (fn [db]
   (:media.active/title db)))

(reg-sub
 :media/active
 :<- [:media]
 :<- [:media.active/title]
 (fn [[media active-title]]
   (get media active-title)))

(reg-sub
 :media.active/poster-url
 :<- [:media/active]
 :<- [:themoviedb.images/base-url]
 :<- [:themoviedb/config]
 (fn [[data base-url config] [_ width]]
   (when (and data config base-url)
     (let [path (-> data :search-result :poster_path)
           poster-sizes (into #{} (-> config :images :poster_sizes))]
       (when-let [size (get poster-sizes width)]
         (str base-url size (-> data :search-result :poster_path)))))))
