(ns qmedia-front.icons
  (:require [tincture.icons :as icons]))

(defn imdb
  [{:keys [class]}]
  [icons/svg-icon {:class class
                   :viewbox "0 0 24 28"}
   [:path {:d "M14.406 12.453v2.844c0 0.562 0.109 1.078-0.594 1.062v-4.828c0.688 0 0.594 0.359 0.594 0.922zM19.344 13.953v1.891c0 0.313 0.094 0.828-0.359 0.828-0.094 0-0.172-0.047-0.219-0.141-0.125-0.297-0.063-2.547-0.063-2.578 0-0.219-0.063-0.734 0.281-0.734 0.422 0 0.359 0.422 0.359 0.734zM2.812 17.641h1.906v-7.375h-1.906v7.375zM9.594 17.641h1.656v-7.375h-2.484l-0.438 3.453c-0.156-1.156-0.313-2.312-0.5-3.453h-2.469v7.375h1.672v-4.875l0.703 4.875h1.188l0.672-4.984v4.984zM16.234 12.875c0-0.469 0.016-0.969-0.078-1.406-0.25-1.297-1.813-1.203-2.828-1.203h-1.422v7.375c4.969 0 4.328 0.344 4.328-4.766zM21.187 15.953v-2.078c0-1-0.047-1.734-1.281-1.734-0.516 0-0.859 0.156-1.203 0.531v-2.406h-1.828v7.375h1.719l0.109-0.469c0.328 0.391 0.688 0.562 1.203 0.562 1.141 0 1.281-0.875 1.281-1.781zM24 4.5v19c0 1.375-1.125 2.5-2.5 2.5h-19c-1.375 0-2.5-1.125-2.5-2.5v-19c0-1.375 1.125-2.5 2.5-2.5h19c1.375 0 2.5 1.125 2.5 2.5z"}]])

(defn tomato
  [{:keys [class]}]
  [icons/svg-icon {:class class
                   :viewbox "0 0 138.75 141.25"}
   [:g {:fill "#f93208"}
    [:path {:d "m20.154 40.829c-28.149 27.622-13.657 61.011-5.734 71.931 35.254 41.954 92.792 25.339 111.89-5.9071 4.7608-8.2027 22.554-53.467-23.976-78.009z"}]
    [:path {:d "m39.613 39.265 4.7778-8.8607 28.406-5.0384 11.119 9.2082z"}]]
   [:path {:d "m39.436 8.5696 8.9682-5.2826 6.7569 15.479c3.7925-6.3226 13.79-16.316 24.939-4.6684-4.7281 1.2636-7.5161 3.8553-7.7397 8.4768 15.145-4.1697 31.343 3.2127 33.539 9.0911-10.951-4.314-27.695 10.377-41.771 2.334.009 15.045-12.617 16.636-19.902 17.076 2.077-4.996 5.591-9.994 1.474-14.987-7.618 8.171-13.874 10.668-33.17 4.668 4.876-1.679 14.843-11.39 24.448-11.425-6.775-2.467-12.29-2.087-17.814-1.475 2.917-3.961 12.149-15.197 28.625-8.476z" :fill "#02902e"}]
   ]
  )
