(ns qmedia-front.input
  (:require [herb.core :refer [<class defgroup]]
            [reagent.core :as r]
            [debux.cs.core :refer [clog]]
            [goog.dom :as dom]))

(defgroup input-styles
  {:input {:display "none"}})

(defgroup button-styles
  {:button {}})

(defn button []
  [:button  {:on-click (fn []
                         (let [input (dom/getElement "path-input")]
                           (.click input)))
             :class (<class button-styles :button)}
   "Select a location"])

(defn Input []
  (let [path (r/atom nil)]
    (fn []
      [:div
       [button]
       [:input {:type "file"
                :id "path-input"
                :on-change (fn [event]
                             (.log js/console event))
                :webkitdirectory ""
                :directory ""
                :class (<class input-styles :input)}]]))
  )
