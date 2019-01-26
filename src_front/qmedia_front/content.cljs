(ns qmedia-front.content
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [qmedia-front.icons :as icons]
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
          :margin "0 !important"
          :overflow-y "auto"}
   :subheading {:margin 0}})

(defgroup rating-style
  {:icon {:width (px 32)
          :height (px 32)}})

(defn title
  []
  (let [theme @(rf/subscribe [:theme])]
    [sa/Header {:inverted (= theme :dark)
                :text-align "center"
                :size "huge"}
     @(rf/subscribe [:media.active/title])])
  )

(defn ratings
  []
  (let [active @(rf/subscribe [:media/active])]
    [sa/Grid {:centered true}
     [sa/GridRow
      (when-let [rating @(rf/subscribe [:media.active/imdb-rating])]
        [sa/Label {:as "a" :color "yellow" :image true}
         [sa/Image {:src "img/imdb.jpg"}]
         rating
         [sa/LabelDetail @(rf/subscribe [:media.active/imdb-votes])]])
      (when-let [rating @(rf/subscribe [:media.active/rotten-tomato-rating])]
        [sa/Label {:as "a" :color "orange" :image true}
         [sa/Image {:src "img/rotten-tomatoes.png"}]
         rating])
      (when-let [rating @(rf/subscribe [:media.active/moviedb-rating])]
        [sa/Label {:as "a" :color "green" :image true}
         [sa/Image {:src "img/tmdb.png"}]
         rating
         [sa/LabelDetail @(rf/subscribe [:media.active/moviedb-votes])]])
      ]]))

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
