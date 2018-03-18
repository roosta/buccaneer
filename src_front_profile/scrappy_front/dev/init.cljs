(ns ^:figwheel-no-load scrappy-front.init
  (:require
   ;; [figwheel.client :as fw :include-macros true]
   [scrappy-front.core :as core]
   [devtools.core :as devtools]
   [scrappy-front.conf :as conf]))

(enable-console-print!)

(devtools/install!)

#_(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'start-descjop!)

(defn on-jsload
  []
  (core/mount-root conf/setting))

(defn start-descjop! []
  (core/init! conf/setting))

(start-descjop!)
