
(ns app.uppercase (:require ["fs" :as fs] ["path" :as path] [clojure.string :as string]))

(defn list-files! [dir *all-files]
  (doall
   (->> (fs/readdirSync dir)
        (map
         (fn [child]
           (let [child-path (path/join dir child)]
             (cond
               (.isFile (fs/statSync child-path)) (swap! *all-files conj child-path)
               (string/includes? child-path "node_modules") (do)
               (string/includes? child-path ".git") (do)
               (string/includes? child-path ".sass-cache") (do)
               :else (list-files! child-path *all-files))))))))

(def some-uppercase #"[A-Z]")

(defn main! []
  (let [*all-files (atom #{})]
    (list-files! (or (aget js/process.argv 2) ".") *all-files)
    (doall
     (->> @*all-files
          (filter (fn [x] (some? (re-find some-uppercase x))))
          (sort)
          (map (fn [x] (println x)))))
    (println)
    (println "Finished")))
