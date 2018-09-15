(ns qmedia-front.subs
  (:require
   [clojure.string :as str]
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
 :media/titles
 :<- [:media]
 (fn [media]
   (set (map :title media))))

(reg-sub
 :media/map
 :<- [:media]
 (fn [media [_ title]]
   (filter #(= title (:title %)) media)))
