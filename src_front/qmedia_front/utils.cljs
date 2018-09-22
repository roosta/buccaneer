(ns qmedia-front.utils
  (:require [soda-ash.core :as sa]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [re-frame.core :as rf]))


(defn create-img-url
  []
  (let [config @(rf/subscribe [:moviedb-config])]
    (clog config)
    )
  )
