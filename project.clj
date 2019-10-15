(defproject buccaneer "0.1.0-SNAPSHOT"
  :description "View media meta data"
  :url "https://github.com/roosta/buccaneer"
  :license {:name "The MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.8.1"]
                 [ring/ring-core "1.7.1"]
                 [tincture "0.3.3-SNAPSHOT" :exclusions [herb]]
                 [cljsjs/react-virtualized "9.21.1-0"]
                 [soda-ash "0.83.0"]
                 [herb "0.10.1-SNAPSHOT"]
                 [cljs-ajax "0.8.0"]
                 [hodgepodge "0.1.3"]
                 [clojure-csv/clojure-csv "2.0.2"]
                 [re-frame "0.10.9"]

                 ;; dev
                 [day8.re-frame/re-frame-10x "0.4.3"]
                 [figwheel-sidecar "0.5.19"]
                 [figwheel "0.5.19"]
                 [philoskim/debux "0.5.6"]
                 [binaryage/devtools "0.9.10"]
                 ]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-cooper "1.2.2"]
            [lein-figwheel "0.5.19"]
            [lein-externs "0.1.6"]
            [lein-shell "0.5.0"]]


  :source-paths ["src"]

  :aliases {"release" ["do"
                       ["clean"]
                       ["cljsbuild" "once" "frontend-release"]
                       ["cljsbuild" "once" "electron-release"]]
            "externs" ["externs" "frontend-release" "resources/cljs-externs/gen.js"]}

  :clean-targets ^{:protect false} ["resources/main.js"
                                    "resources/public/js/ui-core.js"
                                    "resources/public/js/ui-core.js.map"
                                    "resources/public/js/ui-out"]

  :figwheel {:http-server-root "public"
             :css-dirs ["resources/public/css"]
             :ring-handler tools.figwheel-middleware/app
             :server-port 3449}

  :cljsbuild {:builds
              [{:source-paths ["electron_src"]
                :id "electron-dev"
                :compiler {:output-to "resources/main.js"
                           :output-dir "resources/public/js/electron-dev"
                           :optimizations :simple
                           :pretty-print true
                           :cache-analysis true}}
               {:source-paths ["ui_src" "dev_src"]
                :id "frontend-dev"
                :figwheel {:on-jsload dev.core/on-jsload}
                :compiler {:output-to "resources/public/js/ui-core.js"
                           :output-dir "resources/public/js/ui-out"
                           :source-map true
                           :preloads [day8.re-frame-10x.preload]
                           :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                           :asset-path "js/ui-out"
                           :optimizations :none
                           :cache-analysis true
                           :main "dev.core"}}
               {:source-paths ["electron_src"]
                :id "electron-release"
                :compiler {:output-to "resources/main.js"
                           :output-dir "resources/public/js/electron-release"
                           :externs ["cljs-externs/common.js"]
                           :optimizations :advanced
                           :cache-analysis true
                           :infer-externs true}}
               {:source-paths ["ui_src" "prod_src"]
                :id "frontend-release"
                :compiler {:output-to "resources/public/js/ui-core.js"
                           :output-dir "resources/public/js/ui-release-out"
                           :source-map "resources/public/js/ui-core.js.map"
                           :externs ["cljs-externs/common.js"  "cljs-externs/gen.js"]
                           :optimizations :advanced
                           :cache-analysis true
                           :infer-externs true
                           :process-shim false
                           :main "prod.core"}}]})
