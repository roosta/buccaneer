(ns qmedia-front.init
    (:require [qmedia-front.core :as core]
              [qmedia-front.conf :as conf]))

(enable-console-print!)

(defn start-descjop! []
  (core/init! conf/setting))

(start-descjop!)
