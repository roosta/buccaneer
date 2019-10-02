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

(def moviedb-base-url "https://api.themoviedb.org/3/")
(def omdb-base-url "http://www.omdbapi.com/")

(def colorthief (nodejs/require "colorthief"))

(reg-fx :fs/media fs/effect)

(reg-fx
 :moviedb/search-movie
 (fn [{:keys [title year]
       :or {year false}}]
   (let [url (str moviedb-base-url "search/movie")]
     (GET url {:params {:api_key (env :moviedb-api-key)
                        :query title
                        :year year}
               :response-format :json
               :error-handler #(rf/dispatch [:set-error %])
               :keywords? true
               :handler #(rf/dispatch [:moviedb/store-movie title %])}))))

(reg-fx
 :moviedb/config
 (fn []
   (let [url (str moviedb-base-url "configuration")]
     (GET url {:params {:api_key (env :moviedb-api-key)}
               :response-format :json
               :keywords? true
               :error-handler #(rf/dispatch [:set-error %])
               :handler #(rf/dispatch [:write-to :themoviedb/config %])}))))

(reg-fx
 :omdb/search-movie
 (fn [{:keys [title year]}]
   (GET omdb-base-url {:params {:apikey (env :omdb-api-key)
                                :t title
                                :y year}
                       :response-format :json
                       :keywords? true
                       :error-handler #(rf/dispatch [:set-error %])
                       :handler #(rf/dispatch [:omdb/store-movie title %])})))

(reg-fx
 :color/primary
 (fn [[url title]]
   (let [config @(rf/subscribe [:themoviedb/config])
         base-url (-> config :images :base_url)]
     (when config
       (-> (.getColor colorthief (str base-url "w300" url))
           (.then #(rf/dispatch [:write-color title (js->clj %)]))
           (.catch #(rf/dispatch [:set-error %])))))))
