(ns qmedia-front.sidebar
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer-macros [<class defgroup]]
             [tincture.core :as t]
             [tincture.grid :refer [Grid]]
             [tincture.icons :as icons]
             [tincture.typography :refer [Typography]]
             [tincture.cssfns :refer [rgb]]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [soda-ash.core :as sa]
             [reagent.debug :refer [log]]
             [cljs.nodejs :as nodejs]
             [re-frame.core :as rf]))


(defn border-color
  []
  (let [theme @(rf/subscribe [:theme])]
    (case theme
      :dark "1px solid rgba(255,255,255,.08)"
      :light "1px solid rgba(34,36,38,.1)")))

(defgroup sidebar-style
  (let [theme @(rf/subscribe [:theme])]
    {:menu
     {:overflow-y "auto"
      :height "100vh"
      :background (case theme
                    :dark "#1B1C1D"
                    :light "#fff")

      :border-radius "0 !important"}}))

(defn active-background-color []
  (let [theme @(rf/subscribe [:theme])]
    (case theme
      :dark (rgb 255 255 255 0.15)
      :light (rgb 0 0 0 0.05))))

(defgroup menu-item-style
  (let [theme @(rf/subscribe [:theme])]
    {:container
     ^{:pseudo {:hover {:cursor "pointer"
                        :background (case theme
                                      :dark (rgb 255 255 255 0.08)
                                      :light (rgb 0 0 0 0.03))}}}
     {:border-top (border-color)
      :background (if (first args)
                    (active-background-color)
                    "inherit")}
     :title {:padding [[(px 10) (px 16)]]}})
  )

(defgroup series-style
  (let [theme @(rf/subscribe [:theme])
        line-color (case theme
                     :dark "rgba(255,255,255,.08)"
                     :light "rgba(34,36,38,.1)")]
    {:title {:position "relative"
             :display "flex"
             :align-items "center"}
     :icon  {:color (case theme
                      :dark "white"
                      :light "black")
             :width (px 24)
             :height (px 24)
             :position "absolute"
             :right "0"}
     :container {}
     :nested-item {:padding-left "24px !important"}
     :collapsing-container {:height 0
                            :overflow "hidden"}}))

(defn collapse
  [open?]
  (let [base {:max-height "0px"
              :overflow "hidden"
              :transition (t/create-transition {:properties ["max-height"]
                                                :durations ["400ms"]
                                                :easings [:ease-out-cubic]})
              :border "none"}]
    (with-meta
      (if open?
        (merge base {:max-height "5000px"
                     :transition (t/create-transition {:properties ["max-height"]
                                                       :durations ["400ms"]})})
        base)
      {:key open?})))


(defn menu-item [{:keys [on-click active class]}]
  (let [theme @(rf/subscribe [:theme])]
    [Grid {:item true
           :on-click on-click
           :xs 12
           :class [(<class menu-item-style :container active) class]}
     (into [Typography {:color theme
                  :variant :subtitle1
                  :class (<class menu-item-style :title)}]
           (r/children (r/current-component)))])
  )
(defn on-click
  [title data]
  (rf/dispatch [:media.active/set-title title data]))

(defn series-item
  [title {:keys [parsed]}]
  (let [open? (r/atom false)]
    (fn []
      [:div {:class (<class series-style :container)}
       [menu-item {:class (<class series-style :title)
                   :on-click #(swap! open? not)}
        title
        (if @open?
          [icons/ExpandLess {:class (<class series-style :icon)}]
          [icons/ExpandMore {:class (<class series-style :icon)}])]
       [Grid {:container true
              :class (<class collapse @open?)}
        (doall
         (for [p parsed]
           (let [sub-title (str (:title p) " - S" (:season p) "E" (:episode p))]
             ^{:key (:full p)}
             [menu-item {:class (<class series-style :nested-item)}
              sub-title])))]])))

(defn movie-item
  [title data]
  (let [active-title @(rf/subscribe [:media.active/title])]
    [menu-item {:on-click #(on-click title data)
                :active (= active-title title)}
     title]))

(defn sidebar
  []
  (let [media @(rf/subscribe [:media])]
    [Grid {:container true
           :class (<class sidebar-style :menu)}
     (doall
      (for [[title data] media]
        (if (:movie? data)
          ^{:key title}
          [movie-item title data]
          ^{:key title}
          [series-item title data]
          )))]))
