(ns qmedia-front.sidebar
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer-macros [<class defgroup]]
             [soda-ash.core :as sa]
             [reagent.debug :refer [log]]
             [cljs.nodejs :as nodejs]
             [re-frame.core :as rf]))

(defgroup sidebar-style
  {:container
   {:background-color "#333"
    :color "#eee"
    :height "100vh"}})

(defn sidebar
  []
  (let [titles @(rf/subscribe [:media/titles])]
    [sa/Menu {:vertical true
              :fluid true
              ;; :inverted true
              }
     (for [t titles]
       [sa/MenuItem {:name t}])]
    #_[:div {:class (<class sidebar-style :container)}
     "asd"]))
