(ns qmedia-front.sidebar
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer-macros [<class defgroup]]
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
              :border "none"}]
    (if open?
      (merge base {:max-height "auto"
                   :border-top "1px solid rgba(34,36,38,.1)"})
      base)))

(defn group-item
  [title]
  (let [open? (r/atom false)
        obj @(rf/subscribe [:media/map title])]
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
            ^{:key sub-title}
            [sa/MenuItem {:class (<class group-item-style :nested-item)}
             sub-title]))]]))
  )

(defn sidebar
  []
  (let [titles @(rf/subscribe [:media/titles])]
    [sa/Menu {:vertical true
              :class (<class sidebar-style :container)
              :fluid true}
     (doall
      (for [title (sort titles)]
        (let [obj @(rf/subscribe [:media/map title])]
          (if (> (count obj) 1)
            ^{:key title}
            [group-item title]
            ^{:key title}
            [sa/MenuItem {:name title}]))
        ))]))
