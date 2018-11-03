(ns qmedia-front.core
  (:require-macros [qmedia-front.macros :refer [env]])
  (:require  [reagent.core :as r]
             [herb.core :as herb :refer-macros [<class <id defgroup]]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [reagent.debug :refer [log]]
             [garden.units :refer [px]]
             [soda-ash.core :as sa]
             [qmedia-front.subs]
             [qmedia-front.events]
             [qmedia-front.content :refer [content]]
             [qmedia-front.sidebar :refer [sidebar]]
             ;; [cljs.nodejs :as nodejs]
             [qmedia-front.view :refer [view]]
             [re-frame.core :as rf]))

(def global-style
  (list [:body {:box-sizing "border-box"
                :margin 0
                :font-size (px 14)
                :font-family ["Lato" "Helvetica Neue" "Arial" "Helvetica" "sans-serif"]}]))

(defgroup root-styles
  {:container {:background-color "#262626"}})

(defn root-component []
  (let [path @(rf/subscribe [:root-dir])]
    [sa/Container {:fluid true
                   :class (<class root-styles :container)}
     [sa/Grid
      [sa/GridRow {:style {:padding-bottom 0}}
       [sa/GridColumn {:width 4}
        [sidebar]]
       [sa/GridColumn {:width 12}
        [content]]]]]))

(defn mount-root [setting]
  (r/render [root-component]
                  (.getElementById js/document "app")))

(defn init! [setting]
  (rf/dispatch-sync [:initialize-db])
  (herb/global-style! global-style)
  (mount-root setting))
