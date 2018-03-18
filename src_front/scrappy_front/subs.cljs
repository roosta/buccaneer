(ns scrappy-front.subs
  (:require
   [re-frame.core :refer [dispatch reg-event-db reg-sub reg-event-fx]]))

(reg-sub
 :root-dir
 (fn [db]
   (:root-dir db)))
