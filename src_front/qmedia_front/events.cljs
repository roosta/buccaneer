(ns qmedia-front.events
  (:require [qmedia-front.db :as db]
            ;; [ajax.core :as ajax]
            [reagent.debug :refer [log error]]
            [qmedia-front.fs :as fs]
            [clojure.string :as str]
            [cljs.core :as cljs]
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

(reg-fx :media fs/effect)

(reg-event-db
 ::set-media
 (fn [db [_ files]]
   (let [media (doall
                (map (fn [file]
                       (let [m (-> (ptn (:basename file))
                                   (js->clj :keywordize-keys true))]
                         (into file m)))
                     files))]
     (assoc db :media media))))

(reg-event-db
 ::set-error
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
    :media {:dir (:root-dir db/default-db)
            :on-success [::set-media]
            :on-failure [::set-error]}}
   ))
