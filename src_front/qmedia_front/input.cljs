(ns qmedia-front.input
  (:require [herb.core :refer [<class defgroup]]
            [re-frame.core :as rf]
            [soda-ash.core :as sa]
            [reagent.core :as r]
            [goog.object :as gobj]
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
                             (let [input (dom/getElement "path-input")
                                   path (-> input
                                            (gobj/get "files")
                                            (gobj/get 0)
                                            (gobj/get "path"))]
                               (when path
                                 (rf/dispatch [:root-dir/set path]))))
                :webkitdirectory ""
                :directory ""
                :class (<class input-styles :input)}]]))
  )
