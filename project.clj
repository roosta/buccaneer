(defproject qmedia "0.1.0-SNAPSHOT"
  :description "View media meta data"
  :url "https://github.com/roosta/qmedia"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339" :exclusions [org.apache.ant/ant]]
                 [org.clojure/core.async "0.4.474"]
                 [reagent "0.8.1"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [ring/ring-core "1.7.0"]
                 [tincture "0.1.5-SNAPSHOT"]
                 [cljs-ajax "0.7.4"]
                 [philoskim/debux-stubs "0.5.1"]
                 [hodgepodge "0.1.3"]
                 [soda-ash "0.82.2"]
                 [clojure-csv/clojure-csv "2.0.2"]
                 [re-frame "0.10.6"]
                 [org.clojure/tools.reader "1.3.0"]]

  :profiles {:dev {:dependencies [[day8.re-frame/re-frame-10x "0.3.3-react16"]
                                  [figwheel-sidecar "0.5.16"]
                                  [figwheel "0.5.16"]
                                  [philoskim/debux "0.5.1"]
                                  [binaryage/devtools "0.9.10"]]

                   :plugins [[lein-figwheel "0.5.16" :exclusions [org.clojure/core.cache]]]}}

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-externs "0.1.6"]
            [lein-shell "0.5.0"]]

  :source-paths ["src_tools" "src_front"]
  :aliases {"descjop-help" ["new" "descjop" "help"]
            "descjop-version" ["new" "descjop" "version"]
            "descjop-init" ["do"
                            ["shell" "npm" "install"]
                            ["shell" "grunt" "download-electron"]]
            "descjop-init-win" ["do"
                            ["shell" "cmd.exe" "/c" "npm" "install"]
                            ["shell" "cmd.exe" "/c" "grunt" "download-electron"]]
            "descjop-externs" ["do"
                               ["externs" "dev-main" "app/dev/js/externs.js"]
                               ["externs" "dev-front" "app/dev/js/externs_front.js"]
                               ["externs" "prod-main" "app/prod/js/externs.js"]
                               ["externs" "prod-front" "app/prod/js/externs_front.js"]]
            "descjop-externs-dev" ["do"
                                   ["externs" "dev-main" "app/dev/js/externs.js"]
                                   ["externs" "dev-front" "app/dev/js/externs_front.js"]]
            "descjop-externs-prod" ["do"
                                    ["externs" "prod-main" "app/prod/js/externs.js"]
                                    ["externs" "prod-front" "app/prod/js/externs_front.js"]]
            "descjop-figwheel" ["trampoline" "figwheel" "dev-front"]
            "descjop-once" ["do"
                            ["cljsbuild" "once" "dev-main"]
                            ["cljsbuild" "once" "dev-front"]
                            ["cljsbuild" "once" "prod-main"]
                            ["cljsbuild" "once" "prod-front"]]
            "descjop-once-dev" ["do"
                                ["cljsbuild" "once" "dev-main"]
                                ["cljsbuild" "once" "dev-front"]]
            "descjop-once-prod" ["do"
                                 ["cljsbuild" "once" "prod-main"]
                                 ["cljsbuild" "once" "prod-front"]]
            ;; electron packager for production
            "descjop-uberapp-osx" ["shell" "electron-packager" "./app/prod" "qmedia" "--platform=darwin" "--arch=x64" "--electron-version=1.6.6"]
            "descjop-uberapp-app-store" ["shell" "electron-packager" "./app/prod" "qmedia" "--platform=mas" "--arch=x64" "--electron-version=1.6.6"]
            "descjop-uberapp-linux" ["shell" "electron-packager" "./app/prod" "qmedia" "--platform=linux" "--arch=x64" "--electron-version=1.6.6"]
            "descjop-uberapp-win64" ["shell" "cmd.exe" "/c" "electron-packager" "./app/prod" "qmedia" "--platform=win32" "--arch=x64" "--electron-version=1.6.6"]
            "descjop-uberapp-win32" ["shell" "cmd.exe" "/c" "electron-packager" "./app/prod" "qmedia" "--platform=win32" "--arch=ia32" "--electron-version=1.6.6"]}

  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :dev-main :compiler :output-dir]
   [:cljsbuild :builds :dev-main :compiler :output-to]
   [:cljsbuild :builds :dev-front :compiler :output-dir]
   [:cljsbuild :builds :dev-front :compiler :output-to]
   [:cljsbuild :builds :prod-main :compiler :output-to]
   [:cljsbuild :builds :prod-main :compiler :output-dir]
   [:cljsbuild :builds :prod-front :compiler :output-dir]
   [:cljsbuild :builds :prod-front :compiler :output-to]]

  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds {:dev-main {:source-paths ["src"]
                                  :jar true
                                  :compiler {:output-to "app/dev/js/cljsbuild-main.js"
                                             :externs ["app/dev/js/externs.js"
                                                       "node_modules/closurecompiler-externs/path.js"
                                                       "node_modules/closurecompiler-externs/process.js"]
                                             :target :nodejs
                                             :output-dir "app/dev/js/out_main"
                                             :optimizations :simple
                                             ;;:source-map "app/dev/js/test.js.map"
                                             :pretty-print true}}
                       :dev-front {:source-paths ["src_front" "src_front_profile/qmedia_front/dev"]
                                   :figwheel {:on-jsload qmedia-front.init/on-jsload}
                                   :jar true
                                   :compiler {:output-to "app/dev/js/front.js"
                                              :externs ["app/dev/js/externs_front.js"]
                                              :main qmedia-front.init
                                              :asset-path "js/out_front"
                                              :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                                              :preloads [day8.re-frame-10x.preload]
                                              :optimizations :none
                                              :output-dir "app/dev/js/out_front"
                                              ;;:source-map "app/dev/js/test.js.map"
                                              :pretty-print true}}
                       :prod-main {:source-paths ["src"]
                                   :jar true
                                   :compiler {:output-to "app/prod/js/cljsbuild-main.js"
                                              :externs ["app/prod/js/externs.js"
                                                        "node_modules/closurecompiler-externs/path.js"
                                                        "node_modules/closurecompiler-externs/process.js"]
                                              :elide-asserts true
                                              :target :nodejs
                                              :output-dir "app/prod/js/out_main"
                                              :optimizations :simple
                                              :output-wrapper true}}
                       :prod-front {:source-paths ["src_front" "src_front_profile/qmedia_front/prod"]
                                    :jar true
                                    :compiler {:output-to "app/prod/js/front.js"
                                               :externs ["app/prod/js/externs_front.js"]
                                               :elide-asserts true
                                               :output-dir "app/prod/js/out_front"
                                               :optimizations :simple
                                               ;;:source-map "app/prod/js/test.js.map"
                                               :output-wrapper true}}}}
  :figwheel {:http-server-root "public"
             :ring-handler figwheel-middleware/app
             :server-port 3449})
