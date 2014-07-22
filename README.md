# sitemap

Clojure library for generating sitemaps.

[![Build Status](https://travis-ci.org/hashobject/sitemap.png)](https://travis-ci.org/hashobject/sitemap)
[![Dependencies Status](http://jarkeeper.com/hashobject/sitemap/status.png)](http://jarkeeper.com/hashobject/sitemap)

Sitemaps XML format described on [http://www.sitemaps.org/](http://www.sitemaps.org/protocol.html).

Sitemap library accepts Clojure datastructure and produces sitemap XML as string.

Optionally sitemap XML can be saved to file using provided path.

Input data structure should be in the following format:

```
[
  {:loc "http://hashobject.com/about"
   :lastmod "2013-05-31"
   :changefreq "monthly"
   :priority "0.8"}
  {:loc "http://hashobject.com/team"
   :lastmod "2013-06-01"
   :changefreq "monthly"
   :priority "0.9"}]
```

So it should be sequence of hash maps. Each map should have following keys:

  * loc - url to the page
  * lastmod - date when page was modified in YYYY-MM-DD format
  * changefreq - how often page content will be changed (daily, weekly, monthly, never)
  * priority - what is the priority of this page on the site (from 0 to 1)


Note that only 'loc' key is mandatory.

Please refer to documentation for values' formats of each key.


## Install

```
[sitemap "0.2.4"]
```

## Usage

```clojure
user=> (use 'sitemap.core)
nil
user=> (generate-sitemap [{:loc "http://hashobject.com/about"
                         :lastmod "2013-05-31"
                         :changefreq "monthly"
                         :priority "0.8"}
                        {:loc "http://hashobject.com/team"
                         :lastmod "2013-06-01"
                         :changefreq "monthly"
                         :priority "0.9"}])
<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">
  <url>
    <loc>http://hashobject.com/about</loc>
    <lastmod>2013-05-31</lastmod>
    <changefreq>monthly</changefreq>
    <priority>0.8</priority>
  </url>
  <url>
    <loc>http://hashobject.com/team</loc>
    <lastmod>2013-06-01</lastmod>
    <changefreq>monthly</changefreq>
    <priority>0.9</priority>
  </url>
</urlset>
```

## Tips

We recommend you to validate your sitemap before submitting it to Google Webmaster tools.
You can use this library and there are also plenty of online validators. 

## Validation

This library can validate the generated XML against [version 0.9][http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd] of the schema.

```clojure
(use 'sitemap.core)
(import 'java.io.File)

(->> 
  (generate-sitemap [{:loc "http://example.com/about"
                      :lastmod "2014-07-22"
                      :changefreq "monthly"
                      :priority "0.8"}])
  (save-sitemap (File. "/tmp/sitemap.xml"))
  (validate-sitemap)
  (count)
  (format "You have %d errors"))

; "You have 0 errors"
```

Validation errors are reported as a list of Exceptions:

```clojure
(->> 
  (generate-sitemap [{:loc "http://example.com/about"
                      :lastmod "2000-00-00"
                      :changefreq "monthly"
                      :priority "0.8"}])
  (save-sitemap (File. "/tmp/sitemap-bad.xml"))
  (validate-sitemap)
  (map #(.getMessage %))
  (first)
  (format "Your first error is %s"))

; "Your first error is cvc-datatype-valid.1.2.3: '2000-00-00' is not a valid value of union type 'tLastmod'."
```

## Contributions

We love contributions. Please submit your pull requests.


## License

Copyright © 2013-2014 Hashobject Ltd (team@hashobject.com).

Distributed under the [Eclipse Public License](http://opensource.org/licenses/eclipse-1.0).
