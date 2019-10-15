(ns ui.events
  (:require [ui.db :as db]
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
 :moviedb/store-media
 (fn [{:keys [db]} [_ title query-result]]
   (let [results (:results query-result)]
     (if (seq results)
       (let [m (if (= (:total_results query-result) 1)
                 (first results)
                 (last (sort-by :vote_count results)))]
         {:db (assoc-in db [:results title :moviedb/search-result] m)
          :color/primary [(:backdrop_path m) title]})
       {:db db}))))

(reg-event-db
 :omdb/store-media
 (fn [db [_ title query-result]]
   (if (not= (:Response query-result) "False")
     (assoc-in db [:results title :omdb/search-result] query-result)
     db)))

(reg-event-db
 :write-to
 (fn [db [_ kw data]]
   (assoc db kw data)))

(reg-event-db
 :write-color
 (fn [db [_ title rgb]]
   (assoc-in db [:colors title] rgb)))

(reg-event-db
 :cleanup
 (fn [db [_ path]]
   (assoc-in db path nil)))

(reg-event-db
 ::set-files
 (fn [db [_ files]]
   (let [files (->>
                (mapv (fn [file]
                       (let [m (-> (ptn (:basename file))
                                   (js->clj :keywordize-keys true))]
                         (into file m)))
                     files)
                (group-by :title)
                (mapv (fn [[k v]]
                        (cond-> {:parsed v
                                 :title k
                                 :movie? true}
                          (> (count v) 1) (assoc :movie? false)))))]
     (-> (assoc db :files files)
         (assoc :loading? false)))))

(reg-event-db
 :set-error
 (fn [db [_ e]]
   (error e)
   (-> (assoc db :error e)
       (assoc :loading? false))))

;; add handler ::fetch-files
;; try to fetch from local storage
;; else fire files fx

(reg-event-fx
 :initialize-db
 (fn []
   {:db db/default-db
    :moviedb/config nil}))

(reg-event-fx
 :root-dir/set
 (fn [{:keys [db]} [_ v]]
   {:db (-> (assoc db :root-dir v)
            (assoc :loading? true))
    :fs/files {:dir v
               :on-success [::set-files]
               :on-failure [:set-error]}}))

(reg-event-fx
 :active/set
 (fn [{:keys [db]} [_ media index]]
   (let [m {:db (assoc db :active/title (:title media)
                       :active/index index
                       :active/media media)}
         year (-> media :parsed first :year)
         title (:title media)
         moviedb (get-in db [:results title :moviedb/search-result])
         omdb (get-in db [:results title :omdb/search-result])]
     (if (:movie? media)
       (cond-> m
         (not moviedb) (assoc :moviedb/search-movie {:title title :year year})
         (not omdb) (assoc :omdb/search-movie {:title title :year year}))
       (cond-> m
         (not moviedb) (assoc :moviedb/search-tv {:title title})
         (not moviedb) (assoc :omdb/search-tv {:title title}))))))

(reg-event-db
 :sidebar.item/toggle-expanded
 (fn [db [_ title]]
   (if (contains? (:sidebar/expanded db) title)
     (update db :sidebar/expanded disj title)
     (update db :sidebar/expanded conj title))))

(reg-event-db
 :sidebar/set-ref
 (fn [db [_ ref]]
   (assoc db :sidebar/ref ref)))

#_(reg-event-db
 :sidebar.item/set-expanded
 (fn [db [_ index]]
   (let [media (-> db :media)]
     ())
   ))
