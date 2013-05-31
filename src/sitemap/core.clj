(ns sitemap.core
  "Library for sitemap generation."
  (:use
      [clojure.java.io]
      [hiccup.core :only (html)]
      [hiccup.page :only (xml-declaration)]))


(defn- xml-header []
  (html (xml-declaration "UTF-8")))


(defn- generate-url-entry [entry]
  [:url
    [:loc (:loc entry)]
    [:lastmod (:lastmod entry)]
    [:changefreq (:changefreq entry)]
    [:priority (:priority entry)]])


(defn- generate-url-entries [entries]
  (html
     [:urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
      (map generate-url-entry entries)]))


(defn generate-sitemap
  "Render Clojure data structures to a string of sitemap XML."
  [url-entries]
  (str (xml-header)
       (generate-url-entries url-entries)))


(defn generate-sitemap-and-save
  "Render Clojure data structures to a string of sitemap XML and save it to file."
  [path url-entries]
  (let [sitemap-xml (generate-sitemap url-entries)]
    (spit path sitemap-xml)
    sitemap-xml))