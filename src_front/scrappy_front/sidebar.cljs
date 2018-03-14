(ns scrappy-front.sidebar
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer [<class]]
             [re-frame.core :as rf]))

(defn sidebar-style
  [component]
  (with-meta
    (component {:container
                {:background-color "#333"
                 :color "#eee"
                 :width (percent 20)
                 :height "100vh"}})
    {:key component}))

(defn sidebar
  []
  [:div {:class (<class sidebar-style :container)}
   "asd"])
