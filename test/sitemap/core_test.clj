(ns sitemap.core-test
  (:use clojure.test
        sitemap.core))

(def sample-sitemap-xml
  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"><url><loc>http://hashobject.com/about</loc><lastmod>2013-05-31</lastmod><changefreq>monthly</changefreq><priority>0.8</priority></url><url><loc>http://hashobject.com/team</loc><lastmod>2013-06-01</lastmod><changefreq>monthly</changefreq><priority>0.9</priority></url></urlset>")


(def sample-sitemap-xml-without-optional-fields
  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"><url><loc>http://hashobject.com/about</loc></url><url><loc>http://hashobject.com/team</loc><changefreq>yearly</changefreq></url></urlset>")


(deftest correct-sitemap-generation-test
  (testing "Sitemap was generate incorrectly."
    (is (= sample-sitemap-xml (generate-sitemap [{:loc "http://hashobject.com/about"
                         :lastmod "2013-05-31"
                         :changefreq "monthly"
                         :priority "0.8"}
                        {:loc "http://hashobject.com/team"
                         :lastmod "2013-06-01"
                         :changefreq "monthly"
                         :priority "0.9"}])))))


(deftest correct-sitemap-saved-to-file-test
  (testing "Sitemap was generate incorrectly or problem occured during saving to file."
    (is (= sample-sitemap-xml (generate-sitemap-and-save "./resources/sitemap.xml"
                [{:loc "http://hashobject.com/about"
                 :lastmod "2013-05-31"
                 :changefreq "monthly"
                 :priority "0.8"}
                {:loc "http://hashobject.com/team"
                 :lastmod "2013-06-01"
                 :changefreq "monthly"
                 :priority "0.9"}])))))


(deftest entry-without-optional-fields-test
  (testing "Sitemap was generate incorrectly because of erros with optional fields."
    (is (= sample-sitemap-xml-without-optional-fields (generate-sitemap [{:loc "http://hashobject.com/about"}
                        {:loc "http://hashobject.com/team"
                         :changefreq "yearly"}])))))