# sitemap

Clojure library for generating sitemaps.

[![Build Status](https://travis-ci.org/hashobject/sitemap.png)](https://travis-ci.org/hashobject/sitemap)

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

So it should be sequence of hash maps. Each map should have following keys: `loc`, `lastmod`,
`changefreq`, `priority`. Only `loc` key is mandatory.

Please refer to documentation for values' formats of each key.


## Install

```
[sitemap "0.2.0"]
```

## Usage

```
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
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"><url><loc>http://hashobject.com/about</loc><lastmod>2013-05-31</lastmod><changefreq>monthly</changefreq><priority>0.8</priority></url><url><loc>http://hashobject.com/team</loc><lastmod>2013-06-01</lastmod><changefreq>monthly</changefreq><priority>0.9</priority></url></urlset>"
```

## Tips

We recommend you validate sitemap before submitting it to Google Webmaster tools.
There are plenty of online validators.


## License

Copyright © 2013 HashObject Ltd (team@hashobject.com).

The use and distribution terms for this software are covered by the Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0) which can be found in the file epl-v10.html at the root of this distribution.

By using this software in any fashion, you are agreeing to be bound by the terms of this license.

You must not remove this notice, or any other, from this software.
