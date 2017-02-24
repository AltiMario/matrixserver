(ns matrixserver.util
  (:require [taoensso.timbre :as timbre]
            [cheshire.core :as json]
            [clj-http.client :as cl]
            [clojure.edn :as edn]))


(defn uuid
  "generates an unique random number"
  []
  (.toString (java.util.UUID/randomUUID)))


(def create-slave-url
  "take the info from the config file"
  (try
    (let [config (edn/read-string (slurp "config/config.edn"))]
      (str "http://" (get-in config [:slave :address]) ":" (get-in config [:slave :port]) "/v1/slave/elaborate"))
    (catch Exception e (timbre/error "Error open the configuration file:" (.getMessage e)))))


(defn- call-slave-api
  "POST via JSON, the parameters to the slave api"
  [url fparams]
  (json/decode
    (let [http-content {:body           (json/encode fparams)
                        :headers        {"X-Api-Version" "2"}
                        :content-type   :json
                        :socket-timeout 15000  ;15 secs of timeout out  
                        :conn-timeout   15000
                        :accept         :json}
          data (cl/post url http-content)]
      (if (= 200 (:status data))
        (:body data)
        (timbre/warn url (:status data) (:body data)))) true))


(defn send-to-slave
  "send the data to elaborate to the slave"
  [content]
  (call-slave-api create-slave-url content))
