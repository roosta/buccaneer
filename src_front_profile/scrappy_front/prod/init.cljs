(ns scrappy-front.init
    (:require [scrappy-front.core :as core]
              [scrappy-front.conf :as conf]))

(enable-console-print!)

(defn start-descjop! []
  (core/init! conf/setting))

(start-descjop!)
