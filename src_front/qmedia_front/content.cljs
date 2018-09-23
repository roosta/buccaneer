(ns qmedia-front.content
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer-macros [<class defgroup]]
             [tincture.core :as t]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [soda-ash.core :as sa]
             [tincture.typography :refer [typography]]
             [reagent.debug :refer [log]]
             [cljs.nodejs :as nodejs]
             [re-frame.core :as rf]))

(defn text-color
  []
  (let [theme @(rf/subscribe [:theme])]
    (case theme
      :dark "rgba(255,255,255,.9)"
      :light "rgba(0,0,0,.95)")))

(defgroup root-style
  {:column {:flex-basis "50%"
            :display "flex"
            :justify-content "center"
            :flex-direction "column"
            :align-items "center"}
   :container {:height "100%"
               :color (text-color)
               :flex-basis "80%"
               :display "flex"
               :overflow-y "auto"}})

(defn title
  []
  [typography {:variant :display2}
   @(rf/subscribe [:media.active/title])]
  )

(defn ratings
  []
  (let [active @(rf/subscribe [:media/active])]
    (clog active)
    [:div "hello"]
    )

  )

(defn content
  []
  (let [active @(rf/subscribe [:media/active])]
    [:div {:class (<class root-style :container)}
     [:div {:class (<class root-style :column)}
      [title]
      [ratings]

      ]
     [:div {:class (<class root-style :column)}
      [sa/Image {:src @(rf/subscribe [:media.active/poster-url "w500"])}]]]))
