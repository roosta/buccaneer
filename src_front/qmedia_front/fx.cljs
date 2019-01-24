(ns qmedia-front.fx
  (:require-macros [qmedia-front.macros :refer [env]])
  (:require [qmedia-front.db :as db]
            [ajax.core :refer [GET POST]]
            [reagent.debug :refer [log error]]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [qmedia-front.fs :as fs]
            [clojure.string :as str]
            [cljs.nodejs :as nodejs]
            [re-frame.core
             :refer [reg-fx
                     dispatch
                     reg-event-db
                     reg-sub
                     reg-event-fx
                     reg-cofx]
             :as rf]))

(def base-url "https://api.themoviedb.org/3/")

(reg-fx :fs/media fs/effect)

(reg-fx
 :moviedb/search-movie
 (fn [{:keys [title year]
       :or {year false}}]
   (let [url (str base-url "search/movie")]
     (GET url {:params {:api_key (env :moviedb-api-key)
                        :query title
                        :year year}
               :response-format :json
               :error-handler #(rf/dispatch [::set-error %])
               :keywords? true
               :handler #(rf/dispatch [:store-movie title %])}))))

(reg-fx
 :moviedb/config
 (fn []
   (let [url (str base-url "configuration")]
     (GET url {:params {:api_key (env :moviedb-api-key)}
               :response-format :json
               :keywords? true
               :error-handler #(rf/dispatch [::set-error %])
               :handler #(rf/dispatch [:write-to :themoviedb/config %])}))))
