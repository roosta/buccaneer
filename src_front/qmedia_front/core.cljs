(ns qmedia-front.core
  (:require  [reagent.core :as r]
             [herb.core :as herb :refer-macros [<class <id]]
             [garden.units :refer [px]]
             [soda-ash.core :as sa]
             [qmedia-front.subs]
             [qmedia-front.events]
             [qmedia-front.sidebar :refer [sidebar]]
             [cljs.nodejs :as nodejs]
             [qmedia-front.view :refer [view]]
             [re-frame.core :as rf]))

(def global-style
  (list [:body {:background "#eee"
                :box-sizing "border-box"
                :margin 0
                :font-size (px 14)
                :font-family ["Lato" "Helvetica Neue" "Arial" "Helvetica" "sans-serif"]}]))

(defn root-styles
  [component]
  (with-meta
    (component
     {:column {:padding-bottom 0}})
    {:key component}))

(defn root-component []
  (let [path @(rf/subscribe [:root-dir])]
    [sa/Container {:fluid true}
     [sa/Grid
      [sa/GridColumn {:id (<id root-styles :column)
                      :width 4}
       [sidebar]]
      [sa/GridColumn {:width 12}
       (pr-str path)
       ]]
     ]))

(defn mount-root [setting]
  (r/render [root-component]
                  (.getElementById js/document "app")))

(defn init! [setting]
  (rf/dispatch [:initialize-db])
  (herb/global-style! global-style)
  (mount-root setting))
