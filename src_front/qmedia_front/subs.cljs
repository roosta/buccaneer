(ns qmedia-front.subs
  (:require
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
