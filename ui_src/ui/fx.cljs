(ns ui.fx
  (:require-macros [tools.env :refer [env]])
  (:require [ui.db :as db]
            [ajax.core :refer [GET POST]]
            [reagent.debug :refer [log error]]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [ui.fs :as fs]
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
(def shell (.-shell (nodejs/require "electron")))

(reg-fx :fs/files fs/effect)

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
               :handler #(rf/dispatch [:moviedb/store-media title %])}))))

(reg-fx
 :moviedb/search-tv
 (fn [{:keys [title]}]
   (let [url (str moviedb-base-url "search/tv")]
     (GET url {:params {:api_key (env :moviedb-api-key)
                        :query title}
               :response-format :json
               :error-handler #(rf/dispatch [:set-error %])
               :keywords? true
               :handler #(rf/dispatch [:moviedb/store-media title %])}))))

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
                                :type "movie"
                                :y year}
                       :response-format :json
                       :keywords? true
                       :error-handler #(rf/dispatch [:set-error %])
                       :handler #(rf/dispatch [:omdb/store-media title %])})))

(reg-fx
 :omdb/search-tv
 (fn [{:keys [title]}]
   (GET omdb-base-url {:params {:apikey (env :omdb-api-key)
                                :t title
                                :type "series"}
                       :response-format :json
                       :keywords? true
                       :error-handler #(rf/dispatch [:set-error %])
                       :handler #(rf/dispatch [:omdb/store-media title %])})))
(reg-fx
 :color/primary
 (fn [[url title]]
   (let [config @(rf/subscribe [:themoviedb/config])
         base-url (-> config :images :base_url)]
     (when (and url config)
       (-> (.getColor colorthief (str base-url "w300" url))
           (.then #(rf/dispatch [:write-color title (js->clj %)]))
           (.catch #(rf/dispatch [:set-error %])))))))

(reg-fx
 :file/open
 (fn [file]
   (let [path (:full file)]
     (.openItem shell path))))
