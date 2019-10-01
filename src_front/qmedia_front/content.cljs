(ns qmedia-front.content
  (:require-macros [garden.def :refer [defcssfn]])
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [qmedia-front.icons :as icons]
             [herb.core :refer-macros [<class defgroup]]
             [tincture.core :as t]
             [tincture.grid :refer [Grid]]
             [tincture.typography :refer [typography]]
             [tincture.cssfns :refer [linear-gradient rgb url]]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [soda-ash.core :as sa]
             [reagent.debug :refer [log]]
             [cljs.nodejs :as nodejs]
             [re-frame.core :as rf]))

(defgroup root-style
  {:image-container
   ^{:pseudo {:after {:content "''"
                      :position "absolute"
                      :left 0
                      :top 0
                      :bottom 0
                      :right 0
                      :background-image (linear-gradient "to bottom" (rgb 0 0 0 0) "0%" (rgb 38 38 38 1) "100%")}}}
   {:position "absolute !important"
    :left 0
    :right 0
    :top 0}
   :image {:max-width "100%"}
   :container {:position "relative"
               :overflow-y "hidden"}
   :grid {:height "100vh"
          :margin "0 !important"
          :overflow-y "auto"}
   :subheading {:margin 0}})

(defgroup rating-style
  {:icon {:margin-right (px 8)
          :color :yellow
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
  (let [theme @(rf/subscribe [:theme])]
    [sa/Grid {:vertical-align "middle"}
     [sa/GridRow
      [sa/GridColumn {:width 12}
       [typography {:variant :h1
                    :color :dark}
        @(rf/subscribe [:media.active/title])]]
      [sa/GridColumn {:width 4}
       [typography {:variant :h3
                    :color theme}
        @(rf/subscribe [:media.active/year])]
       ]]]))

(defn ratings []
  (let [theme @(rf/subscribe [:theme])]
    [:div {:class (<class rating-style :container)}
     (when-let [rating @(rf/subscribe [:media.active/imdb-rating])]
       [:div {:class (<class rating-style :column)}
        [icons/imdb {:class (<class rating-style :icon)}]
        [typography {:class (<class rating-style :rating)
                     :variant :h6
                     :color theme}
         (str rating " / " @(rf/subscribe [:media.active/imdb-votes]))]])
     (when-let [rating @(rf/subscribe [:media.active/moviedb-rating])]
       [:div {:class (<class rating-style :column)}
        [icons/tmdb {:class (<class rating-style :tmdb-icon)}]
        [typography {:variant :h6
                     :color theme
                     :class (<class rating-style :rating)}
         (str rating " / " @(rf/subscribe [:media.active/moviedb-votes]))]])
     (when-let [rating @(rf/subscribe [:media.active/rotten-tomato-rating])]
       [:div {:class (<class rating-style :column)}
        [icons/tomato {:class (<class rating-style :icon)}]
        [typography {:variant :h6
                     :color theme
                     :class (<class rating-style :rating)}
         rating]])
     ]))

(defn info []
  (let [theme @(rf/subscribe [:theme])]
    [sa/Grid {:vertical-align "middle"
              :class (<class info-styles :grid)}
     [sa/GridRow
      [sa/GridColumn {:width 6}
       [ratings]]
      (when-let [runtime @(rf/subscribe [:media.active/runtime])]
        [sa/GridColumn {:width 4}
         [typography {:variant :h6
                      :color theme}
          runtime]])
      (when-let [genre @(rf/subscribe [:media.active/genre])]
        [sa/GridColumn {:width 4}
         [typography {:variant :h6
                      :align :right
                      :color theme}
          genre]])]]))

(defn content []
  (let [active @(rf/subscribe [:media/active])]
    (when active
      [:div {:class (<class root-style :container)}
       [:div {:class (<class root-style :image-container)}
        [:img {:class (<class root-style :image)
               :src @(rf/subscribe [:media.active/backdrop-url "original"])}]]
       [Grid {:container true
              :justify :center
              :align-items :center
              :class (<class root-style :grid)}
        [Grid {:item true
               :xs 9}
         [title]
         [info]]]])))
