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
  {:background {:position "absolute"
                :left 0
                :background-size "contain"
                :background-repeat "no-repeat"
                :right 0
                :bottom 0
                :top 0
                :background-image (url @(rf/subscribe [:media.active/backdrop-url "original"]))}
   :gradient {:position "absolute"
              :background (linear-gradient "to bottom" (rgba 0 0 0 0) "0%" (rgba 38 38 38 1) "70%")
              :left 0
              :right 0
              :top 0
              :bottom 0}
   :container {:position "relative"}
   :grid {:color (text-color)
          :height "100vh"
          :margin "0 !important"
          :overflow-y "auto"}
   :subheading {:margin 0}})

(defgroup rating-style
  {:icon {:margin-right (px 12)
          :width (px 32)
          :height (px 32)}
   :rating {:margin 0}})

(defn title
  []
  (let [theme @(rf/subscribe [:theme])]
    [typography {:variant :display3}
     @(rf/subscribe [:media.active/title])]
    #_[sa/Header {:inverted (= theme :dark)
                :text-align "center"
                :size "huge"}
     @(rf/subscribe [:media.active/title])])
  )

(defn ratings
  []
  (let [active @(rf/subscribe [:media/active])]
    [sa/Grid
     [sa/GridRow
      (when-let [rating @(rf/subscribe [:media.active/imdb-rating])]
        [sa/GridColumn
         [:div {:style {:display "flex"
                        :align-items "center"}}
          [icons/imdb {:class (<class rating-style :icon)}]
          [typography {:class (<class rating-style :rating)
                       :variant :subheading}
           (str rating " / " @(rf/subscribe [:media.active/imdb-votes]))]]]

        #_[sa/Label {:as "a" :color "yellow" :image true}
           [sa/Image {:src "img/imdb.jpg"}]
           rating
           [sa/LabelDetail @(rf/subscribe [:media.active/imdb-votes])]])
      #_(when-let [rating @(rf/subscribe [:media.active/rotten-tomato-rating])]
          [sa/Label {:as "a" :color "orange" :image true}
           [sa/Image {:src "img/rotten-tomatoes.png"}]
           rating])
      #_(when-let [rating @(rf/subscribe [:media.active/moviedb-rating])]
          [sa/Label {:as "a" :color "green" :image true}
           [sa/Image {:src "img/tmdb.png"}]
           rating
           [sa/LabelDetail @(rf/subscribe [:media.active/moviedb-votes])]])
      ]]))

(defn content
  []
  (let [active @(rf/subscribe [:media/active])]
    (when active
      [:div {:class (<class root-style :container)}
       [:div {:class (<class root-style :background)}]
       [:div {:class (<class root-style :gradient)}]
       [sa/Grid {:class (<class root-style :grid)}
        [sa/GridRow {:vertical-align "middle"}
         [sa/GridColumn {:width 8
                         :text-align "center"}
          [title]
          [ratings]]
         [sa/GridColumn {:width 8}
          #_[sa/Image {:centered true
                       :src @(rf/subscribe [:media.active/poster-url "original"])}]]]]])))
