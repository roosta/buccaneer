(ns ui.content
  (:require-macros [garden.def :refer [defcssfn]])
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [ui.icons :as icons]
             [herb.core :refer-macros [<class defgroup]]
             [soda-ash.core :as sa]
             [tincture.core :as t]
             [tincture.grid :refer [Grid]]
             [tincture.typography :refer [Typography]]
             [tincture.cssfns :refer [linear-gradient rgb url]]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [cljs.nodejs :as nodejs]
             [reagent.debug :refer [log]]
             [re-frame.core :as rf]))

(def colorthief (nodejs/require "colorthief"))

(defn backdrop-container-style [primary-rgb]
  (let [[r g b] primary-rgb]
    ^{:pseudo {:after {:content "''"
                       :position "absolute"
                       :left 0
                       :top 0
                       :bottom 0
                       :right 0
                       :background-image (linear-gradient "to bottom" (rgb 0 0 0 0) "0" (rgb r g b 1) "85%")}}}
    {:position "absolute"
     :left 0
     :height "100%"
     :right 0
     :z-index 1
     :top 0}))

(defn image-style []
  {:width "100%"
   :z-index 1})

(defgroup root-style
  {:container {:position "relative"
               :overflow-y "hidden"}
   :grid {:height "100vh"
          :overflow-y "auto"}
   :column {:z-index 2}
   :subheading {:margin 0}})

(defgroup rating-style
  (let [brightness @(rf/subscribe [:color.primary/brightness])]
    {:icon {:margin-right (px 8)
            :color (if (= brightness :dark)
                     "#FBB829"
                     "#D29004")
            :width (px 32)
            :height (px 32)}
     :tmdb-icon {:margin-right (px 8)
                 :width (px 24)
                 :color (if (= brightness :dark)
                          "#01d277"
                          "#01844B")
                 :height (px 24)}
     :column {:display :flex
              :margin-right (px 16)
              :align-items :center}
     :container {:display :flex}
     :rating {:margin 0}}))

(defgroup info-styles
  {:grid {:padding [[(px 16) 0]]}})

(defn title []
  (let [brightness @(rf/subscribe [:color.primary/brightness])]
    [Grid {:container true
           :align-items :flex-end
           :justify :space-between}
     [Grid {:item true
            :xs 9}
      [Typography {:variant :h2
                   :color brightness}
       @(rf/subscribe [:active/title])]]
     [Grid {:item true
            :x 3}
      [Typography {:variant :h4
                   :color brightness}
       @(rf/subscribe [:active/year])]
      ]]))

(defn ratings []
  (let [brightness @(rf/subscribe [:color.primary/brightness])]
    [:div {:class (<class rating-style :container)}
     (when-let [rating @(rf/subscribe [:active/imdb-rating])]
       [:div {:class (<class rating-style :column)}
        [icons/imdb {:class (<class rating-style :icon)}]
        [Typography {:class (<class rating-style :rating)
                     :variant :h6
                     :no-wrap true
                     :color brightness}
         (str rating " / " @(rf/subscribe [:active/imdb-votes]))]])
     (when-let [rating @(rf/subscribe [:active/moviedb-rating])]
       [:div {:class (<class rating-style :column)}
        [icons/tmdb {:class (<class rating-style :tmdb-icon)}]
        [Typography {:variant :h6
                     :color brightness
                     :no-wrap true
                     :class (<class rating-style :rating)}
         (str rating " / " @(rf/subscribe [:active/moviedb-votes]))]])
     (when-let [rating @(rf/subscribe [:active/rotten-tomato-rating])]
       [:div {:class (<class rating-style :column)}
        [icons/tomato {:class (<class rating-style :icon)}]
        [Typography {:variant :h6
                     :color brightness
                     :no-wrap true
                     :class (<class rating-style :rating)}
         rating]])]))

(defn info []
  (let [brightness @(rf/subscribe [:color.primary/brightness])]
    [Grid {:container true
           :justify :space-between
           :align-items :center
           :class (<class info-styles :grid)}
     [Grid {:item true
            :xs 5}
      [ratings]]
     (when-let [runtime @(rf/subscribe [:active/runtime])]
       [Typography {:variant :h6
                    :color brightness}
        runtime])
     (when-let [genre @(rf/subscribe [:active/genre])]
       [Grid {:item true
              :xs 3}
        [Typography {:variant :h6
                     :align :right
                     :color brightness}
         genre]])]))

(defn description-style []
  {:padding-bottom (px 16)})

(defn description []
  (let [brightness @(rf/subscribe [:color.primary/brightness])]
    (when-let [description @(rf/subscribe [:active/description])]
      [Typography {:variant :subtitle1
                   :class (<class description-style)
                   :color brightness}
       description]))
   )

(defn backdrop []
  (let [url @(rf/subscribe [:active/backdrop-url "original"])
        rgb @(rf/subscribe [:color/primary])]
    (when rgb
      [:div {:class (<class backdrop-container-style rgb)}
       [:img {:class (<class image-style)
              :src url}]])))

(defn play-button []
  (let [brightness @(rf/subscribe [:color.primary/brightness])]
    [sa/Button {:inverted (= brightness :dark)
                :on-click #(rf/dispatch [:active/play])
                :icon true}
     [sa/Icon {:name "play"}]]))

(defn content []
  (let [active @(rf/subscribe [:active/media])]
    (when active
      [:div {:class (<class root-style :container)}
       [backdrop]
       [Grid {:container true
              :justify :center
              :align-items :center
              :class (<class root-style :grid)}
        [Grid {:item true
               :class (<class root-style :column)
               :xs 9}
         [title]
         [info]
         [description]
         [play-button]]]])))
