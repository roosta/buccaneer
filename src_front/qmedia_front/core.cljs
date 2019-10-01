(ns qmedia-front.core
  (:require-macros [qmedia-front.macros :refer [env]])
  (:require  [reagent.core :as r]
             [herb.core :as herb :refer-macros [<class <id defgroup defglobal]]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [tincture.core :as t]
             [tincture.grid :refer [Grid]]
             [tincture.cssfns :refer [rgb]]
             [reagent.debug :refer [log]]
             [garden.units :refer [px]]
             [qmedia-front.subs]
             [qmedia-front.events]
             [qmedia-front.fx]
             [qmedia-front.content :refer [content]]
             [qmedia-front.sidebar :refer [sidebar]]
             ;; [cljs.nodejs :as nodejs]
             [qmedia-front.view :refer [view]]
             [re-frame.core :as rf]))

(defglobal global-style
  [:body {:box-sizing "border-box"
          :margin 0}]
  ["::-webkit-scrollbar" {:-webkit-appearance "none"
                          :width "10px"
                          :height "10px"}]
  ["::-webkit-scrollbar-track" {:background (rgb 255 255 255 0.1)
                                :border-radius "0px"}]
  ["::-webkit-scrollbar-thumb" {:cursor "pointer"
                                :border-radius (px 5)
                                :background (rgb 255 255 255 0.25)
                                :transition "color 0.2sec ease"}])

(defgroup root-style
  {:container {:background-color "#262626"}})

(defn root-component []
  (let [path @(rf/subscribe [:root-dir])]
    [Grid {:container true
           :class (<class root-style :container)}
     [Grid {:item true
            :xs 2}
      [sidebar]]
     [Grid {:item true
            :xs 10}
      [content]]]))

(defn mount-root [setting]
  (r/render [root-component]
                  (.getElementById js/document "app")))

(defn init! [setting]
  (rf/dispatch-sync [:initialize-db])
  (mount-root setting)
  (t/init!))
