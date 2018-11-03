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
  {:grid {:color (text-color)
          :height "100vh"
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
    (when active
      [sa/Grid {:class (<class root-style :grid)}
       [sa/GridRow {:vertical-align "middle"}
        [sa/GridColumn {:width 8
                        :text-align "center"}
         [title]
         [ratings]]
        [sa/GridColumn {:width 8}
         [sa/Image {:centered true
                    :src @(rf/subscribe [:media.active/poster-url "w500"])}]]]])))
