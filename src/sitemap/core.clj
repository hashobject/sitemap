(ns sitemap.core
  (:use
      [hiccup.core :only (html)]
      [hiccup.page :only (xml-declaration)])

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
