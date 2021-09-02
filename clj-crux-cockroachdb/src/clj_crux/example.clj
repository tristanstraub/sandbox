(ns clj-crux.example
  (:require [crux.api :as crux]
            [clojure.string :as str]))

(def db-user
  (System/getenv "DB_USER"))

(defn crux-cockroach
  []
  (let [db-user (or db-user "maxroach")]
    (crux/start-node {:crux.jdbc/connection-pool {:dialect {:crux/module 'crux.jdbc.psql/->dialect}
                                                  :db-spec {:host    "localhost"
                                                            :port    26257
	                                                    :dbname  "bank"
	                                                    :user    db-user
                                                            :sslmode "require"
                                                            :ssl     true
                                                            :sslcert (str/replace "roach1/certs/client.{}.crt"
                                                                                  "{}"
                                                                                  db-user)
                                                            :sslkey  (str/replace "roach1/certs/client.{}.key.pk8"
                                                                                  "{}"
                                                                                  db-user)}}
	              :crux/tx-log {:crux/module 'crux.jdbc/->tx-log
	                            :connection-pool :crux.jdbc/connection-pool}
	              :crux/document-store {:crux/module 'crux.jdbc/->document-store
	                                    :connection-pool :crux.jdbc/connection-pool}})))

(def manifest
  {:crux.db/id :manifest
   :things ["x" "y" "z"]})

(defn -main
  [& _]
  (let [crux (crux-cockroach)
        tx   (crux/await-tx crux (crux/submit-tx crux [[:crux.tx/put manifest]]))]
    (println (crux/entity (crux/db crux tx) :manifest))
    (.close crux)))
