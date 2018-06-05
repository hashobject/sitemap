(ns sitemap.core
  "Library for sitemap generation and validation."
  (:import
    [java.util.zip GZIPOutputStream])
  (:require
    [clojure.java.io :as io]
    [hiccup.core :refer [html]]
    [hiccup.page :refer [xml-declaration]]
    [sitemap.validator :as v]))

; Sitemaps MUST be UTF-8 encoded - http://www.sitemaps.org/faq.html#faq_output_encoding
(def encoding-utf-8 "UTF-8")

(def chunk-size 50000)

(def ^:dynamic *extension* ".xml")

(def ^:private xml-header
  (delay
    (html (xml-declaration encoding-utf-8))))


(defn- spit-utf8 [path s]
  (spit path s :encoding encoding-utf-8))

(defn- spit-gzipped [path s]
  (with-open [w (-> path
                    io/output-stream
                    GZIPOutputStream.
                    (io/writer :encoding encoding-utf-8))]
    (.write w s)))


(defn- generate-url-entry [entry]
  [:url
   [:loc (:loc entry)]
   (when-let [x (:lastmod entry)]
     [:lastmod x])
   (when-let [x (:changefreq entry)]
     [:changefreq x])
   (when-let [x (:priority entry)]
     [:priority x])])


(defn- generate-url-entries [entries]
  (html
    [:urlset {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
     (map generate-url-entry entries)]))


(defn generate-sitemap
  "Render Clojure data structures to a String of sitemap XML."
  [url-entries]
  (str @xml-header
       (generate-url-entries url-entries)))


(defn generate-sitemap*
  "Render Clojure data structures to a seq of sitemap XMLs, chunked at the maximum sitemap
  size (50,000)."
  [url-entries]
  (->> url-entries
       (partition-all chunk-size)
       (map generate-sitemap)))


(defprotocol SitemapPathGenerator
  (root-path  [_ basename] "Returns the output path for a sitemap or sitemap index.")
  (chunk-path [_ basename i] "Returns the output path for a sitemap 'chunk' (a sub-sitemap of a sitemap index) with index i."))

(def path-generator
  "Default output path generator"
  (reify SitemapPathGenerator
    (root-path [_ basename]
      (str basename *extension*))
    (chunk-path [_ basename i]
      (str basename "-" i *extension*))))


(defn generate-sitemap-index
  "Generates sitemap index XML for the `sitemap-paths`, returned as a string."
  [sitemap-paths]
  (str @xml-header
       (html
         [:sitemapindex {:xmlns "http://www.sitemaps.org/schemas/sitemap/0.9"}
          (map (fn [path]
                 [:sitemap
                  [:loc path]])
               sitemap-paths)])))


(defn generate-sitemap-and-save*
  "Render Clojure data structures to sitemap XML, emitting a sitemap index with sitemap chunks
  when the number of url-entries is greater than permitted for a single sitemap (50,000). The
  output file name(s) will be based on the basename.
  
  Example: (generate-sitemap-and-save* \"https://example.com\" \"dir/sitemap\" url-entries)
  will emit dir/sitemap.xml when count(url-entries) <= 50,000, otherwise will emit
  dir/sitemap.xml (index, pointing to https://example.com/sitemap-0.xml and so on),
  dir/sitemap-0.xml, dir/sitemap-1.xml, and so on."
  ([absolute-root basename url-entries]
   (generate-sitemap-and-save* absolute-root basename url-entries nil))

  ([absolute-root basename url-entries opts]
   (let [{:keys [^SitemapPathGenerator path-generator gzip?]
          :or {path-generator path-generator
               gzip? true}} opts
         spit-fn (or (:spit-fn opts) (if gzip? spit-gzipped spit-utf8))]

     (binding [*extension* (if gzip? (str *extension* ".gz") *extension*)]
       (let [sitemap-xmls (generate-sitemap* url-entries)
             root-path (root-path path-generator basename)]
         (if (= 1 (count sitemap-xmls))
           (spit-fn root-path (first sitemap-xmls))

           (let [sitemap-paths (map #(chunk-path path-generator basename %) (range (count sitemap-xmls)))
                 remote-paths (map #(str absolute-root "/" (.getName (io/file %))) sitemap-paths)]
             (spit-fn root-path (generate-sitemap-index remote-paths))
             (doseq [[xml path] (map vector sitemap-xmls sitemap-paths)]
               (spit-fn path xml)))))))))


(defn generate-sitemap-and-save
  "Render Clojure data structures to a string of sitemap XML and save it to file. Does not
  check whether the number of url-entries is greater than allowed."
  [path url-entries]
  (let [sitemap-xml (generate-sitemap url-entries)]
    (spit-utf8 path sitemap-xml)
    sitemap-xml))


(defn save-sitemap [f sitemap-xml]
  "Save the sitemap XML to a UTF-8 encoded File."
  (spit-utf8 f sitemap-xml))


(defn validate-sitemap [in]
  "Validate a File, String or InputStream that contains an XML sitemap
   against the sitemaps.org schema and return a list of validation errors. 
   If the Sitemap is valid then the list will be empty. If the XML is 
   structurally invalid then throws SAXParseException."
   (v/validate-sitemap in))
