(ns qmedia-front.sidebar
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer-macros [<class defgroup]]
             [tincture.core :as t]
             [cljsjs.react-virtualized]
             [soda-ash.core :as sa]
             [tincture.grid :refer [Grid]]
             [tincture.icons :as icons]
             [tincture.typography :refer [Typography]]
             [tincture.cssfns :refer [rgb]]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [reagent.debug :refer [log]]
             [goog.object :as gobj]
             [cljs.nodejs :as nodejs]
             [re-frame.core :as rf]))


(def virtualized-list (r/adapt-react-class (gobj/get js/ReactVirtualized "List")))
(def auto-sizer (r/adapt-react-class (gobj/get js/ReactVirtualized "AutoSizer")))

(def row-height 45)

(defn border-color
  []
  (let [theme @(rf/subscribe [:theme])]
    (case theme
      :dark "1px solid rgba(255,255,255,.08)"
      :light "1px solid rgba(34,36,38,.1)")))


(defgroup sidebar-style
  (let [theme @(rf/subscribe [:theme])]
    {:menu {:overflow-y "auto"
            :height "100vh"
            :background (case theme
                          :dark "#1B1C1D"
                          :light "#fff")

            :border-radius "0 !important"}
     :list {:outline "none"}}))

(defn active-background-color []
  (let [theme @(rf/subscribe [:theme])]
    (case theme
      :dark (rgb 255 255 255 0.15)
      :light (rgb 0 0 0 0.05))))

(defgroup menu-item-style
  (let [theme @(rf/subscribe [:theme])]
    {:row
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
     :container {}
     :nested-item {:padding-left "24px !important"}
     :collapsing-container {:height 0
                            :overflow "hidden"}}))

(defn collapse
  [open?]
  (let [base {:max-height "0px"
              :overflow "hidden"
              ;; :transition (t/create-transition {:properties ["max-height"]
              ;;                                   :durations ["400ms"]
              ;;                                   :easings [:ease-out-cubic]})

              :border "none"}]
    (with-meta
      (if open?
        (merge base {:max-height "5000px"
                     ;; :transition (t/create-transition {:properties ["max-height"]
                     ;;                                   :durations ["400ms"]})
                     })
        base)
      {:key open?})))


(defn menu-item [{:keys [on-click active class title]}]
  (let [theme @(rf/subscribe [:theme])]
    [Grid {:item true
           :on-click on-click
           :xs 12
           :class [(<class menu-item-style :row active) class]}
     (into [Grid {:container true
                  :align-items :center
                  :justify :space-between}
            [Typography {:color theme
                         :variant :subtitle1
                         :class (<class menu-item-style :title)}
             title]]
           (r/children (r/current-component)))]))

(defn on-click
  [file index]
  (rf/dispatch [:active/set file index]))

;; TODO Light theme
(defgroup icon-styles
  (let [theme @(rf/subscribe [:theme])]
    {:container
     ^{:pseudo {:hover {:transform "scale(1.35)"
                        :background (rgb 255 255 255 0.1)}}}
     {:width (px 32)
      :background (rgb 255 255 255 0)
      :transition (t/create-transition {:property [:transform :background]
                                        :duration 50})
      :border-radius "50%"
      :margin-right (px 8)
      :height (px 32)}
     :icon {:color (case theme
                     :dark (rgb 255 255 255 0.85)
                     :light "black")}})
)

(defn icon-button [open? title]
  [:div {:on-click (fn []
                     (let [ref @(rf/subscribe [:sidebar/ref])]
                       (rf/dispatch-sync [:sidebar.item/toggle-expanded title])
                       (.recomputeRowHeights ref)
                       (.forceUpdate ref)))
         :class (<class icon-styles :container)}
   (if open?
     [icons/ExpandLess {:class (<class icon-styles :icon)}]
     [icons/ExpandMore {:class (<class icon-styles :icon)}])]
  )

(defn series-item
  [file index]
  (let [title (:title file)
        open? @(rf/subscribe [:sidebar.item/expanded? title])]
    [:div {:class (<class series-style :container)}
     [menu-item {:class (<class series-style :title)
                 :title title
                 :on-click (fn [] nil)}
      [icon-button open? title]]
     [Grid {:container true
            :class (<class collapse open?)}
      (doall
       (for [p (:parsed file)]
         (let [sub-title (str (:title p) " - S" (:season p) "E" (:episode p))]
           ^{:key (:full p)}
           [menu-item {:title sub-title
                       :class (<class series-style :nested-item)}])))]]))

(defn movie-item
  [file index]
  (let [file-title (:title file)
        active-title @(rf/subscribe [:active/title])]
    [menu-item {:on-click #(on-click file index)
                :title file-title
                :active (= active-title file-title)}]))

(defn row-renderer [props]
  (let [media @(rf/subscribe [:files])
        {:keys [index style isScrolling isVisible key parent]} (js->clj props :keywordize-keys true)
        {:keys [title movie?] :as file} (get media index)]
    (r/as-element
     [:div {:key key
            :style style}
      (if movie?
        ^{:key title}
        [movie-item file index]
        ^{:key title}
        [series-item file index])])))

(defn index->row-height [params]
  (let [media @(rf/subscribe [:files])
        index (gobj/get params "index")
        {:keys [parsed title]} (get media index)
        expanded? @(rf/subscribe [:sidebar.item/expanded? title])]
    (if expanded?
      (->> row-height
           (* (count parsed))
           (+ row-height))
      row-height)))

(defn sidebar
  []
  (let [media @(rf/subscribe [:files])]
    [:div {:class (<class sidebar-style :menu)}
     [auto-sizer
      (fn [props]
        (r/as-element
         [virtualized-list {:width (gobj/get props "width")
                            :height (gobj/get props "height")
                            :ref #(rf/dispatch [:sidebar/set-ref %])
                            :class (<class sidebar-style :list)
                            :row-count (count media)
                            :row-height index->row-height
                            :row-renderer row-renderer}]))]]))
