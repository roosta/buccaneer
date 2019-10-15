(ns ^:figwheel-no-load dev.core
  (:require [figwheel.client :as fw :include-macros true]
            [devtools.core :as devtools]
            [ui.core :as core]))

(enable-console-print!)

(devtools/install!)

(defn on-jsload []
  (core/mount-root))

;; (fw/watch-and-reload
;;  :websocket-url   "ws://localhost:3449/figwheel-ws"
;;  :jsload-callback (fn [] (print "reloaded")))

(core/init!)
