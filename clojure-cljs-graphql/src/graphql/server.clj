(ns graphql.server
  (:require [integrant.core :as ig]
            [org.httpkit.server :refer [send! with-channel]]
            [clojure.tools.logging :as log]
            graphql.httpkit
            [reitit.ring :as ring]
            [graphql.app]
            [clojure.java.io :as io]
            [reitit.middleware :as middleware]
            [reitit.ring.middleware.multipart :as multipart]

            ;; middleware
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.coercion :as coercion]
            [ring.middleware.params :as params]


            )
  (:gen-class))

(defonce system
  nil)

(defn app
  []
  (ring/ring-handler
   (ring/router [["/" {:get  {:handler (fn [req]
                                         {:status  200
                                          :headers {"Content-Type" "text/html"}
                                          :body    (slurp (io/resource "public/index.html"))})}
                       :post {:handler #'graphql.app/handler}}]
                 ["/*" (ring/create-resource-handler)]]
                {:data {:muuntaja   m/instance
      	                :middleware [params/wrap-params
                                     muuntaja/format-middleware
                                     coercion/coerce-exceptions-middleware
                                     coercion/coerce-request-middleware
                                     coercion/coerce-response-middleware]}})))

(defn start
  []
  (alter-var-root #'system (constantly (ig/init
                                        {:graphql.httpkit/httpkit {:app (app)}}))))

(defn stop
  []
  (alter-var-root #'system (constantly (ig/halt! system))))

(defn -main
  []
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. ^Runnable (fn []
                                         (log/info "Shutting down")
                                         (stop))))
  (start))
