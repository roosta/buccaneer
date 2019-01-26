(ns qmedia-front.subs
  (:require
   [clojure.string :as str]
   [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
   [reagent.debug :refer [log error]]
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
 :theme
 (fn [db]
   (:theme db)))

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
   (when (and (:moviedb/search-result data) config)
     (let [base-url (-> config :images :base_url)
           path (-> data :moviedb/search-result :poster_path)
           sizes (into #{} (-> config :images :poster_sizes))
           size (get sizes width)]
       (if size
         (str base-url size path)
         (error "Unsupported backdrop width, pick one of these: " (str/join ", " sizes)))
       ))))

(reg-sub
 :media.active/backdrop-url
 :<- [:media/active]
 :<- [:themoviedb/config]
 (fn [[data config] [_ width]]
   (when (and (:moviedb/search-result data) config)
     (let [base-url (-> config :images :base_url)
           path (-> data :moviedb/search-result :backdrop_path)
           sizes (into #{} (-> config :images :backdrop_sizes))
           size (get sizes width)]
       (if size
         (str base-url size path)
         (error "Unsupported backdrop width, pick one of these: " (str/join ", " sizes)))))))

(reg-sub
 :media.active/imdb-rating
 :<- [:media/active]
 (fn [data]
   (let [data (:omdb/search-result data)]
     (when data
       (-> data :imdbRating)))))

(reg-sub
 :media.active/imdb-votes
 :<- [:media/active]
 (fn [data]
   (let [data (:omdb/search-result data)]
     (when data
       (-> data :imdbVotes)))))

(reg-sub
 :media.active/rotten-tomato-rating
 :<- [:media/active]
 (fn [data]
   (let [data (:omdb/search-result data)]
     (when data
       (:Value (first (filter (comp #{"Rotten Tomatoes"} :Source) (:Ratings data))))
       ))))
