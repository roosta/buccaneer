(ns scrappy-front.view
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer [<class]]
             [re-frame.core :as rf]))


(defn root-style
  []
  {})

(defn view
  []
  [:div "hello world"])
