(ns qmedia-front.db)

(def default-db
  {:files nil
   :theme :dark
   :loading? false
   :results {}
   :sidebar/expanded #{}
   :active/title nil
   :active/index nil
   :root-dir nil})
