(ns qmedia-front.db)

(def default-db
  {:files nil
   :theme :dark
   :loading? false
   :results nil
   :sidebar/expanded #{}
   :media.active/title nil
   :media.active/index nil
   :root-dir nil})
