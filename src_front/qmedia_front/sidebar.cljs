(ns qmedia-front.sidebar
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer-macros [<class defgroup]]
             [tincture.core :as t]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [soda-ash.core :as sa]
             [reagent.debug :refer [log]]
             [cljs.nodejs :as nodejs]
             [re-frame.core :as rf]))

(defgroup sidebar-style
  {:menu
   {:overflow-y "auto"
    :height "100vh"
    :border-radius "0 !important"}})

(defn border-color
  []
  (let [theme @(rf/subscribe [:theme])]
    (case theme
      :dark "1px solid rgba(255,255,255,.08)"
      :light "1px solid rgba(34,36,38,.1)")))

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
             :position "absolute"
             :right "0"}
     :container {:border-top (border-color)}
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
                                                       :durations ["400ms"]})
                     :border-top (border-color)})
        base)
      {:key open?})))

(defn on-click
  [title data]
  (rf/dispatch [:media.active/set-title title data]))

(defn series-item
  [title {:keys [parsed]}]
  (let [open? (r/atom false)]
    (fn []
      [:div {:class (<class series-style :container)}
       [sa/MenuItem {:class (<class series-style :title)
                     :on-click #(swap! open? not)}
        title
        [sa/Icon {:class (<class series-style :icon)
                  :name (if @open? "caret down" "caret right")}]]
       [:div {:class (<class collapse @open?)}
        (doall
         (for [p parsed]
           (let [sub-title (str (:title p) " - S" (:season p) "E" (:episode p))]
             ^{:key (:full p)}
             [sa/MenuItem {:class (<class series-style :nested-item)}
              sub-title])))]])))

(defn movie-item
  [title data]
  (let [active-title @(rf/subscribe [:media.active/title])]
    [sa/MenuItem {:on-click #(on-click title data)
                  :active (= active-title title)
                  :name title}]))

(defn sidebar
  []
  (let [media @(rf/subscribe [:media])
        theme @(rf/subscribe [:theme])]
    [sa/Menu {:vertical true
              :inverted (= theme :dark)
              :class (<class sidebar-style :menu)
              :fluid true}
     (doall
      (for [[title data] media]
        (if (:movie? data)
          ^{:key title}
          [movie-item title data]
          ^{:key title}
          [series-item title data]
          )))]))
