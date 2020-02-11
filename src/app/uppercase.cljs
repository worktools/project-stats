
(ns app.uppercase
  (:require ["fs" :as fs]
            ["path" :as path]
            [clojure.string :as string]
            [app.util.file :refer [get-dir-files!]]))

(def some-uppercase #"[A-Z]")

(defn main! []
  (let [all-files (get-dir-files! (or (aget js/process.argv 2) "."))]
    (doall
     (->> all-files
          (filter (fn [x] (some? (re-find some-uppercase x))))
          (sort)
          (map (fn [x] (println x)))))
    (println)
    (println "Finished")))
