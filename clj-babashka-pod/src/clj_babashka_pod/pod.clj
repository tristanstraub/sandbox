(ns clj-babashka-pod.pod
  (:require [bencode.core :as bencode]
            [clojure.string :as str]
            [cheshire.core :as json]
            [clojure.java.io :as io])
  (:import java.io.PushbackInputStream))


(defn log
  [& xs]
  (spit "logs.txt" (str (str/join " | " xs) "\n") :append true))

(defonce stdin
  (PushbackInputStream. System/in))

(defn read-stdin []
  (doto (bencode/read-bencode stdin)
    log))

(defn write-stdout [v]
  (bencode/write-bencode System/out v)
  (.flush System/out))

(defn ->string
  [s]
  (doto (String. s)
    log))

(defn invoke
  [s]
  (case (->string (get s "var"))
    "pod.clj-babashka-pod/test!"  (write-stdout {"id"     (get s "id")
                                                 "value"  (.getBytes (json/generate-string [[1] [2]]))
                                                 "status" ["done"]})
    "pod.clj-babashka-pod/watch*" (future (write-stdout {"id"     (get s "id")
                                                         "value"  (.getBytes (json/generate-string [[1] [2]]))
                                                         "status" ["done"]})

                                          (Thread/sleep 1000)

                                          (write-stdout {"id"     (get s "id")
                                                         "value"  (.getBytes (json/generate-string [[1] [2] [3]]))
                                                         "status" ["done"]}))))

(defn process
  [s]
  (case (->string (get s "op"))
    "describe" (write-stdout {"format" "json"
                              "namespaces"
                              [{"name" "pod.clj-babashka-pod"
                                "vars" [{"name" "test!"}
                                        {"name" "watch*"}]}]})
    "invoke" (invoke s)))

(defn -main
  [& argv]
  (log "------")

  (loop []
    (let [s (try
              (read-stdin)
              (catch java.io.EOFException e
                :eof))]
      (when (not= s :eof)
        (process s)
        (recur)))))
