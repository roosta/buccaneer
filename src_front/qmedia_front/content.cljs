(ns qmedia-front.content
  (:require  [reagent.core :as r]
             [garden.units :refer [px percent]]
             [herb.core :refer-macros [<class defgroup]]
             [tincture.core :as t]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [soda-ash.core :as sa]
             [reagent.debug :refer [log]]
             [cljs.nodejs :as nodejs]
             [re-frame.core :as rf]))

(defgroup root-style
  {
   :column {:height "100%"
            :flex-basis "80%"
            }
   :container {:height "100%"
               :overflow-y "auto"}})

(defn content
  []
  (let [active @(rf/subscribe [:media/active])]
    [sa/GridColumn {:class (<class root-style :column)
                    :width 13}
     [:div {:class (<class root-style :container)}
      [sa/Image {:src @(rf/subscribe [:media.active/poster-url "w500"])}]]]))
