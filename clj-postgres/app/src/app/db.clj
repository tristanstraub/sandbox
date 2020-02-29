(ns app.db
  (:require [clojure.java.jdbc :as jdbc])
  (:gen-class))

(defn -main
  [& {:as argv}]
  (let [db {:dbtype   "postgresql"
            :dbname   (get argv "DB_NAME")
            :host     (get argv "DB_HOST")
            :user     (get argv "DB_USER")
            :password (get argv "DB_PASSWORD")}]
    (jdbc/delete! db "users" {})
    (jdbc/insert-multi! db :users [{:name "test1"}
                                   {:name "test2"}])
    (println (jdbc/query db ["select * from users"]))))

(comment
  (-main "DB_NAME" "sandbox"
         "DB_HOST" "localhost"
         "DB_USER" "app_user"
         "DB_PASSWORD" "password")
  )
