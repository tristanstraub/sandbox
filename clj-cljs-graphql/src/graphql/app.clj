(ns graphql.app
  (:require [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]
            [clojure.data.json :as json])
  (:require [clojure.java.io :as io]))

(defn get-hero
  [context arguments value]
  (let [{:keys [episode]} arguments]
    (if (= episode :NEWHOPE)
      {:id 1000
       :name "Luke"
       :home_planet "Tatooine"
       :appears_in ["NEWHOPE" "EMPIRE" "JEDI"]}
      {:id 2000
       :name "Lando Calrissian"
       :home_planet "Socorro"
       :appears_in ["EMPIRE" "JEDI"]})))

(def star-wars-schema
  (-> (slurp (io/resource "schema.edn"))
      edn/read-string
      (attach-resolvers {:get-hero get-hero
                         :get-droid (constantly {})})
      schema/compile))

(defn handler
  [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (let [query (get-in request [:body-params :query])
               result (when query
                        (execute star-wars-schema query nil nil))]
           (json/write-str result))})
