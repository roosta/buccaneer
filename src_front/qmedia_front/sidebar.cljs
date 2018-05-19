(ns qmedia-front.sidebar
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer-macros [<class]]
             [reagent.debug :refer [log]]
             [cljs.nodejs :as nodejs]
             [re-frame.core :as rf]))

(defn sidebar-style
  [component]
  (with-meta
    (component {:container
                {:background-color "#333"
                 :color "#eee"
                 :height "100vh"}})
    {:key component}))

(defn sidebar
  []
  (let [titles @(rf/subscribe [:media/titles])]
    (log titles)
    [:div {:class (<class sidebar-style :container)}
     "asd"]))
