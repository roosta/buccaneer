(ns ui.subs
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
 :active/media
 (fn [db]
   (:active/media db)))

(reg-sub
 :active/results
 :<- [:results]
 :<- [:active/title]
 (fn [[results title]]
   (when (and results title)
     (get results title))))

(reg-sub
 :active.results/moviedb
 :<- [:active/results]
 (fn [results]
   (:moviedb/search-result results)))

(reg-sub
 :active.results/omdb
 :<- [:active/results]
 (fn [results]
   (:omdb/search-result results)))

(reg-sub
 :active/poster-url
 :<- [:active.results/moviedb]
 :<- [:themoviedb/config]
 (fn [[results config] [_ width]]
   (when (and results config)
     (let [base-url (-> config :images :base_url)
           path (:poster_path results)
           sizes (into #{} (-> config :images :poster_sizes))
           size (get sizes width)]
       (if size
         (str base-url size path)
         (error "Unsupported backdrop width, pick one of these: " (str/join ", " sizes)))
       ))))

(reg-sub
 :active/backdrop-url
 :<- [:active.results/moviedb]
 :<- [:themoviedb/config]
 (fn [[results config] [_ width]]
   (when (and results config)
     (let [base-url (-> config :images :base_url)
           path (:backdrop_path results)
           sizes (into #{} (-> config :images :backdrop_sizes))
           size (get sizes width)]
       (if size
         (str base-url size path)
         (error "Unsupported backdrop width, pick one of these: " (str/join ", " sizes)))))))

(reg-sub
 :active/imdb-rating
 :<- [:active.results/omdb]
 (fn [results]
   (when results
     (-> results :imdbRating))))

(reg-sub
 :active/imdb-votes
 :<- [:active.results/omdb]
 (fn [results]
   (when results
     (-> results :imdbVotes))))

(reg-sub
 :active/rotten-tomato-rating
 :<- [:active.results/omdb]
 (fn [results]
   (when results
     (:Value (first (filter (comp #{"Rotten Tomatoes"} :Source) (:Ratings results)))))))

(reg-sub
 :active/moviedb-rating
 :<- [:active.results/moviedb]
 (fn [results]
   (when results
     (:vote_average results))))

(reg-sub
 :active/moviedb-votes
 :<- [:active.results/moviedb]
 (fn [results]
   (when results
     (:vote_count results))))

(reg-sub
 :active/year
 :<- [:active/media]
 (fn [data]
   (-> data :parsed first :year)))

(reg-sub
 :active/runtime
 :<- [:active.results/omdb]
 (fn [results]
   (when results
     (when-let [runtime (:Runtime results)]
       (when-let [n (parse-number (re-find #"\d+" runtime))]
         (let [hours (.floor js/Math (/ n 60))
               minutes (mod n 60)]
           (str hours " hr " minutes " min")))))))

(reg-sub
 :active/genre
 :<- [:active.results/omdb]
 (fn [results]
   (when results
     (when-let [genre (:Genre results)]
       genre))))

(reg-sub
 :active/description
 :<- [:active.results/omdb]
 (fn [results]
   (when results
     (when-let [plot (:Plot results)]
       plot))))

(reg-sub
 :colors
 (fn [db]
   (-> db :colors)))

(reg-sub
 :color/primary
 :<- [:colors]
 :<- [:active/title]
 (fn [[colors title]]
   (get colors title)))

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

(reg-sub
 :active.play/button-disabled?
 :<- [:active/media]
 (fn [media]
   (> (count (:parsed media)) 1)))
