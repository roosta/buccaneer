(ns ui.core
  (:require-macros [tools.env :refer [env]])
  (:require  [reagent.core :as r]
             [herb.core :as herb :refer-macros [<class <id defgroup defglobal]]
             [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
             [tincture.core :as t]
             [soda-ash.core :as sa]
             [tincture.grid :refer [Grid]]
             [tincture.typography :refer [Typography]]
             [ui.input :refer [Input]]
             [tincture.cssfns :refer [rgb]]
             [ui.icons :as icons]
             [reagent.debug :refer [log]]
             [garden.units :refer [px]]
             [ui.subs]
             [ui.events]
             [ui.fx]
             [ui.content :refer [content]]
             [ui.sidebar :refer [sidebar]]
             ;; [cljs.nodejs :as nodejs]
             [ui.view :refer [view]]
             [re-frame.core :as rf]))

(defglobal global-style
  [:body {:box-sizing "border-box"
          :margin 0}]
  ["::-webkit-scrollbar-track" {:background (str "rgba(255,255,255,0.1) !important")}]
  ["::-webkit-scrollbar-thumb" {:background (str "rgba(255,255,255,0.25) !important")}])

(defgroup main-style
  {:container {}})

(defn container-style []
  {:background-color "#262626"
   :height "100vh"})

(defn icon-style []
  {:color "white"
   :width "32px"
   :margin (px 8)
   :height "32px"})

(defn error-component [error]
  [Grid {:class (<class container-style)
         :justify :center
         :align-items :center
         :container true}
   [icons/error {:class (<class icon-style)}]
   [Typography {:variant :h6
                :color :dark}
    (str "Error: " error)]])

(defn load-component []
  [Grid {:class (<class container-style)
         :justify :center
         :container true
         :align-items :center}
   [sa/Dimmer {:active true}
    [sa/Loader]]])

(defn select-path []
  [Grid {:class (<class container-style)
         :justify :center
         :align-items :center
         :container true}
   [Input]]
  )

(defn main-component []
  (let [loading? @(rf/subscribe [:loading?])]
    (if loading?
      [load-component]
      [Grid {:container true
             :class (<class main-style :container)}
       [Grid {:item true
              :xs 2}
        [sidebar]]
       [Grid {:item true
              :xs 10}
        [content]]]))
  )

(defn root-component []
  (let [path @(rf/subscribe [:root-dir])
        error @(rf/subscribe [:error])]
    (cond
      error [error-component error]
      path  [main-component]
      :else [select-path])))

(defn mount-root []
  (r/render [root-component]
                  (.getElementById js/document "app-container")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (mount-root)
  (t/init! {:font-family ["Lato" "Helvetica Neue" "Arial" "Helvetica" "sans-serif"]}))
