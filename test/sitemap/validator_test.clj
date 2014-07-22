(ns sitemap.validator-test
  (:use clojure.test
        sitemap.core)
  (:import [java.io File]
           [org.xml.sax SAXParseException]))

(defn fixture [fixture-name] 
  "Get the XML File representing the test case."
  (File. (apply str ["test-resources/" fixture-name ".xml"])))

(defn validate-and-get-error-messages [fixture-name]
  "Validate the XML File and convert the list of Exceptions into Strings."
  (->>
    (fixture fixture-name)
    (validate-sitemap)
    (map #(.getMessage %))))

(deftest valid-test
  (testing "We can validate a good sitemap."
    (is (= [] (validate-sitemap (fixture "sitemap-valid"))))))

(deftest invalid-test
  (testing "Invalid XML in a bad sitemap fails hard."
    (is (thrown? org.xml.sax.SAXParseException (validate-sitemap (fixture "sitemap-invalid-xml"))))))

(deftest missing-namespace-test
  (testing "Sitemap namespace is mandatory."
    (let [errors (validate-and-get-error-messages "sitemap-missing-ns")]
      (is (.contains (first errors) "Cannot find the declaration of element 'urlset'") (first errors)))))

(deftest wrong-namespace-test
  (testing "Sitemap namespace is mandatory."
    (let [errors (validate-and-get-error-messages "sitemap-wrong-ns")]
      (is (.contains (first errors) "Cannot find the declaration of element 'urlset'") (first errors)))))

(deftest invalid-date-test
  (testing "Date types are validated."
    (let [errors (validate-and-get-error-messages "sitemap-invalid-date")]
      (is (= 2 (count errors)))
      (is (.contains (first errors) "not a valid value of union type 'tLastmod'")))))

(deftest invalid-frequency-test
  (testing "Frequency types are validated."
    (let [errors (validate-and-get-error-messages "sitemap-invalid-frequency")]
      (is (= 2 (count errors)))
      (is (.contains (first errors) "fortnightly' is not facet-valid with respect to enumeration")
        (first errors)))))

