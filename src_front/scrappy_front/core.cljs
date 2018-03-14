(ns scrappy-front.core
  (:require  [reagent.core :as r]
             [herb.core :as herb :refer [<class]]
             [garden.units :refer [px]]
             [scrappy-front.subs]
             [scrappy-front.events]
             [scrappy-front.sidebar :refer [sidebar]]
             [re-frame.core :as rf]))

(def global-style
  (list [:body {:background "#eee"
                :box-sizing "border-box"
                :margin 0
                :font-size (px 14)
                :font-family ["Lato" "Helvetica Neue" "Arial" "Helvetica" "sans-serif"]}]))

(defn root-styles
  []
  {:height "100vh"}
  )

(defn root-component []
  [:div {:class (<class root-styles)}
   [sidebar]])

(defn mount-root [setting]
  (r/render [root-component]
                  (.getElementById js/document "app")))

(defn init! [setting]
  (rf/dispatch [:initialize-db])
  (herb/set-global-style! global-style)
  (mount-root setting))
