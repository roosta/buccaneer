(ns scrappy-front.events
  (:require [scrappy-front.db :as db]
            ;; [ajax.core :as ajax]
            [reagent.debug :refer [log error]]
            [scrappy-front.fs :as fs]
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

(reg-fx :files fs/effect)

(reg-event-db
 ::set-files
 (fn [db [_ files]]
   (assoc db :files files)))

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
    :files {:dir (:root-dir db/default-db)
            :on-success [::set-files]
            :on-failure [::set-error]}}
   ))
