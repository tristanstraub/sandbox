(ns graphql.client
  (:require [rum.core :as rum]
            [goog.dom :as dom]
            [graphql-query.core :refer [graphql-query]]
            [cljs-http.client :as http]
            [clojure.core.async :as async]))

(rum/defc main-panel
  < rum/reactive
  [state]
  [:div
   [:div
    [:label "Hero: "]
    [:span (get-in (rum/react state) [:hero :name])]]
   [:div
    [:label "Episodes: "]
    [:div
     ;; using back-tick to get auto-keying of children
     `[:ul ~@(for [episode (get-in (rum/react state) [:hero :appears_in])]
               [:li episode])]]]])

(defonce state
  (atom nil))

(defn main
  []
  (rum/mount (main-panel state) (dom/getElement "app"))

  (async/go
    (->> (async/<! (http/post "/" {:json-params {:query (graphql-query {:queries [[:hero [:name :appears_in]]]})}}))
         :body
         :data
         (swap! state merge))))

(main)
