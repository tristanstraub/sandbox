(ns graphql.httpkit
  (:require [integrant.core :as ig]
            [org.httpkit.server :as http]
            [clojure.tools.logging :as log]))

(defmethod ig/init-key ::httpkit
  [_ {:keys [app ip port] :as opts :or {ip   "127.0.0.1"
                                        port "8080"}}]
  (let [stop (http/run-server app {:ip   ip
                                   :port (Long/parseLong (str port))})]
    (log/info "Started serving port" port)
    stop))

(defmethod ig/halt-key! ::httpkit
  [_ stop]
  (stop))
