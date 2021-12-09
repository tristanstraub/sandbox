#!/usr/bin/env bb

(require '[babashka.pods :as pods])

(pods/load-pod "./clj-babashka-pod.sh")

(println :namespace? (some? (find-ns 'pod.clj-babashka-pod)))

(require '[pod.clj-babashka-pod])

(pod.clj-babashka-pod/test!)

(defn watch
  ([path cb] (watch path cb {}))
  ([path cb opts]
   (pods/invoke
    "pod.clj-babashka-pod"
    'pod.clj-babashka-pod/watch*
    [path opts]
    {:handlers {:success (fn [event] (cb event))
                :error   (fn [{:keys [:ex-message :ex-data]}]
                           (binding [*out* *err*]
                             (println "ERROR:" ex-message)))}})
   nil))

(let [result (atom [])]
  (watch "/tmp" (fn [r] (swap! result conj r)))

  (while (< (count @result) 2)
    (Thread/sleep 100))
  (println @result))
