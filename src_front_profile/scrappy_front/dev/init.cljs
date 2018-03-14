(ns scrappy-front.init
  (:require [figwheel.client :as fw :include-macros true]
            [scrappy-front.core :as core]
            [devtools.core :as devtools]
            [re-frisk.core :refer [enable-re-frisk!]]
            [scrappy-front.conf :as conf]))

(enable-console-print!)

(devtools/install!)

(enable-re-frisk!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'start-descjop!)

(defn start-descjop! []
  (core/init! conf/setting))

(start-descjop!)
