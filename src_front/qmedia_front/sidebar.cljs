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
    :color "#eee"
    :height "100vh"}})

(defn sidebar
  []
  (let [titles @(rf/subscribe [:media/titles])]
    [sa/Menu {:vertical true
              :fluid true}
     (doall
      (for [title (sort titles)]
        (let [obj @(rf/subscribe [:media/map title])]
          (if (> (count obj) 1)
            ^{:key title}
            [sa/Dropdown {:item true
                          :text title}
             [sa/DropdownMenu
              (for [o obj]
                (let [ext-title (str (:title o) " - S" (:season o) "E" (:episode o))]
                  ^{:key ext-title}
                  [sa/DropdownItem {:text ext-title}]))]]

            ^{:key title}
            [sa/MenuItem {:name title}]))
        ))]))
