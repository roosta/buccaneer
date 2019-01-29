(ns qmedia-front.content
  (:require-macros [garden.def :refer [defcssfn]])
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [qmedia-front.icons :as icons]
             [herb.core :refer-macros [<class defgroup]]
             [tincture.core :as t]
             [tincture.typography :refer [typography]]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [soda-ash.core :as sa]
             [tincture.typography :refer [typography]]
             [reagent.debug :refer [log]]
             [cljs.nodejs :as nodejs]
             [re-frame.core :as rf]))

(defcssfn linear-gradient
  [dir c1 p1 c2 p2]
  [dir [c1 p1] [c2 p2]])

(defcssfn rgba
  [c1 c2 c3 a]
  [c1 c2 c3 a])

(defcssfn url)

(defn text-color
  []
  (let [theme @(rf/subscribe [:theme])]
    (case theme
      :dark "rgba(255,255,255,.9)"
      :light "rgba(0,0,0,.95)")))

(defgroup root-style
  {:image
   ^{:pseudo {:after {:content "''"
                      :position "absolute"
                      :left 0
                      :top 0
                      :bottom 0
                      :right 0
                      :background-image (linear-gradient "to bottom" (rgba 0 0 0 0) "0%" (rgba 38 38 38 1) "100%")}}}
   {:position "absolute !important"
    :left 0
    :right 0
    :top 0}
   :container {:position "relative"
               :overflow-y "hidden"}
   :grid {:color (text-color)
          :height "100vh"
          :margin "0 !important"
          :overflow-y "auto"}
   :subheading {:margin 0}})

(defgroup rating-style
  {:icon {:margin-right (px 8)
          :width (px 32)
          :height (px 32)}
   :tmdb-icon {:margin-right (px 8)
               :width (px 24)
               :height (px 24)}
   :column {:display :flex
            :margin-right (px 16)
            :align-items :center}
   :container {:display :flex}
   :rating {:margin 0}})

(defgroup info-styles
  {:grid {:margin-top "0 !important"}})

(defn title []
  [sa/Grid {:vertical-align "middle"}
   [sa/GridRow
    [sa/GridColumn {:width 12}
     [typography {:variant :display3}
      @(rf/subscribe [:media.active/title])]]
    [sa/GridColumn {:width 4}
     [typography {:variant :display1}
      @(rf/subscribe [:media.active/year])]
     ]]])

(defn ratings []
  [:div {:class (<class rating-style :container)}
   (when-let [rating @(rf/subscribe [:media.active/imdb-rating])]
     [:div {:class (<class rating-style :column)}
      [icons/imdb {:class (<class rating-style :icon)}]
      [typography {:class (<class rating-style :rating)
                   :variant :subheading}
       (str rating " / " @(rf/subscribe [:media.active/imdb-votes]))]])
   (when-let [rating @(rf/subscribe [:media.active/moviedb-rating])]
     [:div {:class (<class rating-style :column)}
      [icons/tmdb {:class (<class rating-style :tmdb-icon)}]
      [typography {:variant :subheading
                   :class (<class rating-style :rating)}
       (str rating " / " @(rf/subscribe [:media.active/moviedb-votes]))]])
   (when-let [rating @(rf/subscribe [:media.active/rotten-tomato-rating])]
     [:div {:class (<class rating-style :column)}
      [icons/tomato {:class (<class rating-style :icon)}]
      [typography {:variant :subheading
                   :class (<class rating-style :rating)}
       rating]])
   ])

(defn info []
  [sa/Grid {:vertical-align "middle"
            :class (<class info-styles :grid)}
   [sa/GridRow
    [sa/GridColumn {:width 6}
     [ratings]]
    (when-let [runtime @(rf/subscribe [:media.active/runtime])]
      [sa/GridColumn {:width 4}
       [typography {:variant :subheading}
        runtime]])
    (when-let [genre @(rf/subscribe [:media.active/genre])]
      [sa/GridColumn {:width 4}
       [typography {:variant :subheading
                    :align :right}
        genre]])]])

(defn content []
  (let [active @(rf/subscribe [:media/active])]
    (when active
      [:div {:class (<class root-style :container)}
       [:div {:class (<class root-style :image)}
        [sa/Image {:fluid true
                   :src @(rf/subscribe [:media.active/backdrop-url "original"])}]]
       [sa/Grid {:class (<class root-style :grid)
                 :centered true}
        [sa/GridRow {:vertical-align "middle"}
         [sa/GridColumn {:width 12}
          [title]
          [info]]]]])))
