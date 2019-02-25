(defproject sitemap "0.4.0"
  :description "Clojure library for sitemap generation."
  :signing {:gpg-key "Hashobject Ltd <team@hashobject.com>"}
  :url "https://github.com/hashobject/sitemap"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["releases"  {:sign-releases false :url "https://clojars.org/repo"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org/hashobject/sitemap"}]]            
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [hiccup "1.0.5"]])
