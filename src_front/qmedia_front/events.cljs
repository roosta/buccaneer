(ns qmedia-front.events
  (:require [qmedia-front.db :as db]
            [reagent.debug :refer [log error]]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [cljs.nodejs :as nodejs]
            [re-frame.core
             :refer [reg-fx
                     dispatch
                     reg-event-db
                     reg-sub
                     reg-event-fx
                     reg-cofx]
             :as rf]))

(def ptn (nodejs/require "parse-torrent-name"))

(reg-event-fx
 :moviedb/store-movie
 (fn [{:keys [db]} [_ title query-result]]
   (let [results (:results query-result)]
     (let [m (if (= (:total_results query-result) 1)
               (first results)
               (last (sort-by :vote_count results)))]
       {:db (assoc-in db [:media title :moviedb/search-result] m)
        :color/primary [(:backdrop_path m) title]}))))

(reg-event-db
 :omdb/store-movie
 (fn [db [_ title query-result]]
   (assoc-in db [:media title :omdb/search-result] query-result)))

(reg-event-db
 :write-to
 (fn [db [_ kw data]]
   (assoc db kw data)))

(reg-event-db
 :write-color
 (fn [db [_ title rgb]]
   (assoc-in db [:media title :color/primary] rgb)))

(reg-event-db
 :cleanup
 (fn [db [_ path]]
   (assoc-in db path nil)))

(reg-event-db
 ::set-media
 (fn [db [_ files]]
   (let [media (->>
                (map (fn [file]
                       (let [m (-> (ptn (:basename file))
                                   (js->clj :keywordize-keys true))]
                         (into file m)))
                     files)
                doall
                (group-by :title)
                (map (fn [[k v]]
                       (if (= (count v) 1)
                         {k {:parsed (first v)
                             :movie? true}}
                         {k {:parsed v
                             :movie? false}})))
                (into {}))]
     (assoc db :media media))))

(reg-event-db
 :set-error
 (fn [db [_ e]]
   (error e)
   (assoc db :error e)))

;; add handler ::fetch-files
;; try to fetch from local storage
;; else fire files fx

(reg-event-fx
 :initialize-db
 (fn []
   {:db db/default-db
    :moviedb/config nil
    :fs/media {:dir (:root-dir db/default-db)
               :on-success [::set-media]
               :on-failure [::set-error]}}))

(reg-event-fx
 :media.active/set-title
 (fn [{:keys [db]} [_ title data]]
   (let [m {:db (assoc db :media.active/title title)}
         year (-> data :parsed :year)
         moviedb (get-in db [:media title :moviedb/search-result])
         omdb (get-in db [:media title :omdb/search-result])]
     (if (:movie? data)
       (cond
         (and (not moviedb) (not omdb))
         (-> m
             (assoc :moviedb/search-movie {:title title :year year})
             (assoc :omdb/search-movie {:title title :year year}))

         (and (not moviedb) omdb)
         (assoc m :omdb/search-movie {:title title :year year})

         (and moviedb (not omdb))
         (assoc m :moviedb/search-movie {:title title :year year})
         :else m)
       m))))
