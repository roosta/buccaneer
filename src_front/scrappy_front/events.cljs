(ns scrappy-front.events
  (:require [scrappy-front.db :as db]
            ;; [ajax.core :as ajax]
            [reagent.debug :refer [log]]
            [re-frame.core :refer [dispatch reg-event-db reg-sub reg-event-fx]]))

(reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))
