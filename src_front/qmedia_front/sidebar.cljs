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
  {:container
   {:background-color "#333"
    :overflow-y "auto"
    :color "#eee"
    :height "100vh"}

   })

(defgroup group-item-style
  {:title {:position "relative"
           :display "flex"
           :align-items "center"}
   :container {:border-top "1px solid rgba(34,36,38,.1)"}
   :nested-item {:padding-left "24px !important"}
   :collapsing-container {:height 0
                          :overflow "hidden"}})

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
                     :border-top "1px solid rgba(34,36,38,.1)"})
        base)
      {:key open?})))

(defn on-click
  [title obj]
  (rf/dispatch [:set-active-title title obj]))

(defn group-item
  [title obj]
  (let [open? (r/atom false)]
    (fn []
      [:div {:class (<class group-item-style :container)}
        [sa/MenuItem {:class (<class group-item-style :title)
                      :on-click #(swap! open? not)}
         title
         [sa/Icon {:style {:color "black"
                           :position "absolute"
                           :right "0"}
                   :name (if @open? "caret down" "caret right")}]]
       [:div {:class (<class collapse @open?)}
        (for [o obj]
          (let [sub-title (str (:title o) " - S" (:season o) "E" (:episode o))]
            ^{:key (:full o)}
            [sa/MenuItem {:class (<class group-item-style :nested-item)}
             sub-title]))]])))

(defn sidebar
  []
  (let [media @(rf/subscribe [:media])]
    [sa/Menu {:vertical true
              :class (<class sidebar-style :container)
              :fluid true}
     (doall
      (for [[title obj] media]
        (if (> (count obj) 1)
          ^{:key title}
          [group-item title obj]
          ^{:key title}
          [sa/MenuItem {:on-click #(on-click title obj)
                        :name title}])))]))
