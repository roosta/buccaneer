(ns qmedia-front.core
  (:require-macros [qmedia-front.macros :refer [env]])
  (:require  [reagent.core :as r]
             [herb.core :as herb :refer-macros [<class <id defgroup defglobal]]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [tincture.core :as t]
             [tincture.grid :refer [Grid]]
             [tincture.typography :refer [Typography]]
             [qmedia-front.input :refer [Input]]
             [tincture.cssfns :refer [rgb]]
             [qmedia-front.icons :as icons]
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
  #_["::-webkit-scrollbar" {:-webkit-appearance "none"
                          :width "10px"
                          :height "10px"}]
  #_["::-webkit-scrollbar-track" {:background (rgb 255 255 255 0.1)
                                :border-radius "0px"}]
  #_["::-webkit-scrollbar-thumb" {:cursor "pointer"
                                :border-radius (px 5)
                                :background (rgb 255 255 255 0.25)}])

(defgroup root-style
  {:container {:background-color "#262626"}})

(defgroup error-style
  {:container {:background-color "#262626"
               :height "100vh"}
   :icon {:color "white"
          :width "32px"
          :margin (px 8)
          :height "32px"}})

(defn error-component [error]
  [Grid {:class (<class error-style :container)
         :justify :center
         :align-items :center
         :container true}
   [icons/error {:class (<class error-style :icon)}]
   [Typography {:variant :h6
                :color :dark}
    (str "Error: " error)]])

(defgroup select-path-styles
  {:container {:background-color "#262626"
               :height "100vh"}})

(defn select-path
  []
  [Grid {:class (<class select-path-styles :container)
         :justify :center
         :align-items :center
         :container true}
   [Input]]
  )

(defn root-component []
  (let [path @(rf/subscribe [:root-dir])
        error @(rf/subscribe [:error])]
    (cond
      error [error-component error]
      path [Grid {:container true
                  :class (<class root-style :container)}
            [Grid {:item true
                   :xs 2}
             [sidebar]]
            [Grid {:item true
                   :xs 10}
             [content]]]
      :else [select-path])))

(defn mount-root [setting]
  (r/render [root-component]
                  (.getElementById js/document "app")))

(defn init! [setting]
  (rf/dispatch-sync [:initialize-db])
  (mount-root setting)
  (t/init! {:font-family ["Lato" "Helvetica Neue" "Arial" "Helvetica" "sans-serif"]}))
