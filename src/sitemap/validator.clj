(ns sitemap.validator
  (:require [clojure.java.io :as io])
  (:import [javax.xml.parsers DocumentBuilderFactory]
           [javax.xml.validation SchemaFactory]
           [javax.xml XMLConstants]
           [java.io File InputStream StringReader]
           [org.xml.sax ErrorHandler InputSource]
           [javax.xml.transform.stream.StreamSource]))

; The latest version of the sitemaps.org schema.
(def sitemap-xsd (io/resource "org/sitemaps/schemas/0.9/sitemap.xsd"))


(defn- read-schema [xsd-url]
  "Read the XSD identified by the URL from the classpath
   into a Schema object."
  (->
    (SchemaFactory/newInstance XMLConstants/W3C_XML_SCHEMA_NS_URI)
    (.newSchema xsd-url)))


(defn new-document-builder [xsd-url]
  "Make a DocumentBuilder that checks namespaces
   and validates against a Schema."
  (->
    (doto 
      (DocumentBuilderFactory/newInstance)
      (.setNamespaceAware true)
      (.setSchema (read-schema xsd-url)))
    (.newDocumentBuilder)))


; http://docs.oracle.com/javase/7/docs/api/org/xml/sax/ErrorHandler.html
(defn new-throwing-error-handler [error-list]
  "Create an error handler that appends Exceptions to a list
   wrapped in an atom."
  (reify
    ErrorHandler
    (warning    [this e] (swap! error-list conj e)); (throw e))
    (error      [this e] (swap! error-list conj e)); (throw e))
    (fatalError [this e] (swap! error-list conj e)))); (throw e))))


(defmulti  parse-xml-document (fn [in db] (class in)))

(defmethod parse-xml-document File [in db] 
  (.parse db in))

(defmethod parse-xml-document InputStream [in db] 
  (.parse db in))

(defmethod parse-xml-document String [in db] 
  (with-open [r (StringReader. in)]
    (.parse db (InputSource. r))))


(defn validate-sitemap [in]
  "Validate a File, String or InputStream containing an XML sitemap."
  (let [errors (atom [])]
    (->>
      (doto
        (new-document-builder sitemap-xsd)
        (.setErrorHandler (new-throwing-error-handler errors)))
      (parse-xml-document in))
     @errors))
