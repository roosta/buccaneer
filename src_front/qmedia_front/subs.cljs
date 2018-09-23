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
 :<- [:themoviedb/config]
 (fn [[data config] [_ width]]
   (let [base-url (-> config :images :base_url)
         path (-> data :search-result :poster_path)
         sizes (into #{} (-> config :images :poster_sizes))
         size (get sizes width)]
     (when (and base-url path size)
       (str base-url size path)))))

(reg-sub
 :media.active/backdrop-url
 :<- [:media/active]
 :<- [:themoviedb/config]
 (fn [[data config] [_ width]]
   (let [base-url (-> config :images :base_url)
         path (-> data :search-result :backdrop_path)
         sizes (into #{} (-> config :images :backdrop_sizes))
         size (get sizes width)]
     (when (and base-url path size)
       (str base-url size path)))))
