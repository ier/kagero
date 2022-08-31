(ns kagero.core
  (:require [datomic.api :as d]))

(comment
  ;; https://docs.datomic.com/on-prem/getting-started/dev-setup.html#run-dev-transactor

  ;; - visit my.datomic.com
  ;; - register using your email, get the licence file from email: https://my.datomic.com/account
  ;; - download latest verion of Datomic from https://my.datomic.com/downloads/free
  ;; - extract zip archive content to ~/bin. In my case the location is "/home/ier/bin/datomic-free-0.9.5703.21"

  ;; 1) cd bin/datomic-free-0.9.5703.21
  ;; 2) bin/transactor config/samples/free-transactor-template.properties
  ;; 3) emacs, cider-jack-in-clj

  ;; or ...

  ;; 1) cd bin/datomic-free-0.9.5703.21/bin
  ;; 2) repl

  ;; Tutorials and manuals:
  ;;       http://www.learndatalogtoday.org/

  ;; Info: http://subhasingh.com/blog/How-to-Setup-Datomic-Free/
  ;;       https://github.com/alexanderkiel/datomic-free
  ;;       https://docs.datomic.com/on-prem/getting-started/transact-data.html
  ;;       https://clojureverse.org/t/a-quick-way-to-start-experimenting-with-datomic/5004
  ;;       https://docs.datomic.com/on-prem/getting-started/dev-setup.html
  ;;       https://docs.datomic.com/on-prem/getting-started/connect-to-a-database.html
  ;;       https://docs.datomic.com/on-prem/getting-started/transact-schema.html
  ;;
  ;; How to get a persisting local datomic database?
  ;; https://stackoverflow.com/a/61328329/404022

  (require '[datomic.api :as d])
  (def db-uri "datomic:mem://hello")
  (d/create-database db-uri)
  (def conn (d/connect db-uri))


  (def movie-schema [{:db/ident :movie/title
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "The title of the movie"}

                     {:db/ident :movie/genre
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "The genre of the movie"}

                     {:db/ident :movie/release-year
                      :db/valueType :db.type/long
                      :db/cardinality :db.cardinality/one
                      :db/doc "The year the movie was released in theaters"}])

  @(d/transact conn movie-schema)


  (def first-movies [{:movie/title "The Goonies"
                      :movie/genre "action/adventure"
                      :movie/release-year 1985}
                     {:movie/title "Commando"
                      :movie/genre "action/adventure"
                      :movie/release-year 1985}
                     {:movie/title "Repo Man"
                      :movie/genre "punk dystopia"
                      :movie/release-year 1984}])

   @(d/transact conn first-movies)


   (def db (d/db conn))

   (def all-movies-q '[:find ?e
                       :where [?e :movie/title]])

   (d/q all-movies-q db)


   (def all-titles-q '[:find ?movie-title
                       :where [_ :movie/title ?movie-title]])

   (d/q all-titles-q db)


   (def titles-from-1985 '[:find ?title
                           :where [?e :movie/title ?title]
                                  [?e :movie/release-year 1985]])

   (d/q titles-from-1985 db)


   (def all-data-from-1985 '[:find ?e ?title ?year ?genre
                             :where [?e :movie/title ?title]
                                    [?e :movie/release-year ?year]
                                    [?e :movie/genre ?genre]
                                    [?e :movie/release-year 1985]])

   (d/q all-data-from-1985 db)


   (d/q '[:find ?e
          :where [?e :movie/title "Commando"]] db)


   (def commando-id (ffirst (d/q '[:find ?e
                                   :where [?e :movie/title "Commando"]]
                                 db)))

   @(d/transact conn [{:db/id commando-id :movie/genre "future governor"}])
   ;; we do not have "future governer"
   (d/q all-data-from-1985 db)


   (def db (d/db conn))
   ;; here we have "future governer" data
   (d/q all-data-from-1985 db)

   ;; TODO: add other steps from the manual

   ;; TODO: show the example with (missing? ...)
  )
