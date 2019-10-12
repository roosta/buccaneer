(ns qmedia-front.db)

(def default-db
  {:media nil
   :theme :dark
   :loading? false
   :sidebar/expanded #{}
   :media.active/title nil
   :media.active/index nil
   :root-dir nil})
