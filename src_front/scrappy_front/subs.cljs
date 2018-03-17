(ns scrappy-front.subs
  (:require
   [re-frame.core :refer [dispatch reg-event-db reg-sub reg-event-fx]]))

(reg-sub
 :path
 (fn [db]
   (:path db)))
