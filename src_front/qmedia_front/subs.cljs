(ns qmedia-front.subs
  (:require
   [clojure.string :as str]
   [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
   [reagent.debug :refer [log error]]
   [reagent.ratom :refer [make-reaction]]
   [cljs.nodejs :as nodejs]
   [re-frame.core :as rf :refer [dispatch reg-event-db reg-sub reg-event-fx reg-sub-raw]]))

(def colorthief (nodejs/require "colorthief"))

(defn parse-number [input]
  (when-not (str/blank? input)
    (let [n (js/parseInt input)]
      (when-not (js/isNaN n)
        (when-not (neg? n) n)))))

(defn brightness
  "http://www.w3.org/TR/AERT#color-contrast"
  [[r g b]]
  (-> (+ (* r 299)
         (* g 587)
         (* b 114))
      (/ 1000)))

(reg-sub
 :root-dir
 (fn [db]
   (:root-dir db)))

(reg-sub
 :files
 (fn [db]
   (:files db)))

(reg-sub
 :results
 (fn [db]
   (:results db)))

(reg-sub
 :themoviedb/config
 (fn [db]
   (:themoviedb/config db)))

(reg-sub
 :active/title
 (fn [db]
   (:active/title db)))

(reg-sub
 :active/index
 (fn [db]
   (:active/index db)))

(reg-sub
 :theme
 (fn [db]
   (:theme db)))

(reg-sub
 :active/file
 (fn [db]
   (-> db :active/file)))

(reg-sub
 :active/results
 :<- [:results]
 :<- [:active/title]
 (fn [[results title]]
   (when (and results title)
     (get results title))))

(reg-sub
 :active/poster-url
 :<- [:active/file]
 :<- [:active/results]
 :<- [:themoviedb/config]
 (fn [[data config] [_ width]]
   (when (and (:moviedb/search-result data) config)
     (let [base-url (-> config :images :base_url)
           path (-> data :moviedb/search-result :poster_path)
           sizes (into #{} (-> config :images :poster_sizes))
           size (get sizes width)]
       (if size
         (str base-url size path)
         (error "Unsupported backdrop width, pick one of these: " (str/join ", " sizes)))
       ))))

(reg-sub
 :active/backdrop-url
 :<- [:active/file]
 :<- [:themoviedb/config]
 (fn [[data config] [_ width]]
   (when (and (:moviedb/search-result data) config)
     (let [base-url (-> config :images :base_url)
           path (-> data :moviedb/search-result :backdrop_path)
           sizes (into #{} (-> config :images :backdrop_sizes))
           size (get sizes width)]
       (if size
         (str base-url size path)
         (error "Unsupported backdrop width, pick one of these: " (str/join ", " sizes)))))))

(reg-sub
 :active/imdb-rating
 :<- [:active/file]
 (fn [data]
   (when-let [data (:omdb/search-result data)]
     (-> data :imdbRating))))

(reg-sub
 :active/imdb-votes
 :<- [:active/file]
 (fn [data]
   (when-let [data (:omdb/search-result data)]
     (-> data :imdbVotes))))

(reg-sub
 :active/rotten-tomato-rating
 :<- [:active/file]
 (fn [data]
   (when-let [data (:omdb/search-result data)]
     (:Value (first (filter (comp #{"Rotten Tomatoes"} :Source) (:Ratings data))))
     )))

(reg-sub
 :active/moviedb-rating
 :<- [:active/file]
 (fn [data]
   (when-let [data (:moviedb/search-result data)]
     (:vote_average data))))

(reg-sub
 :active/moviedb-votes
 :<- [:active/file]
 (fn [data]
   (when-let [data (:moviedb/search-result data)]
     (:vote_count data))))

(reg-sub
 :active/year
 :<- [:active/file]
 (fn [data]
   (-> data :parsed first :year)))

(reg-sub
 :active/runtime
 :<- [:active/file]
 (fn [data]
   (when-let [data (:omdb/search-result data)]
     (when-let [runtime (:Runtime data)]
       (when-let [n (parse-number (re-find #"\d+" runtime))]
         (let [hours (.floor js/Math (/ n 60))
               minutes (mod n 60)]
           (str hours " hr " minutes " min")))))))

(reg-sub
 :active/genre
 :<- [:active/file]
 (fn [data]
   (when-let [data (:omdb/search-result data)]
     (when-let [genre (:Genre data)]
       genre))))

(reg-sub
 :active/description
 :<- [:active/file]
 (fn [data]
   (when-let [data (:omdb/search-result data)]
     (when-let [plot (:Plot data)]
       plot))))

(reg-sub
 :color/primary
 :<- [:active/file]
 (fn [data]
   (-> data :color/primary)))

(reg-sub
 :color.primary/brightness
 :<- [:color/primary]
 (fn [rgb]
   (if (and rgb (>= (brightness rgb) 128))
     :light
     :dark)))

(reg-sub
 :error
 (fn [db]
   (-> db :error)))

(reg-sub
 :loading?
 (fn [db]
   (-> db :loading?)))

(reg-sub
 :sidebar/expanded
 (fn [db]
   (-> db :sidebar/expanded)))

(reg-sub
 :sidebar.item/expanded?
 :<- [:sidebar/expanded]
 (fn [expanded [_ title]]
   (contains? expanded title)))

(reg-sub
 :sidebar/ref
 (fn [db]
   (-> db :sidebar/ref)))
