
(ns app.collector
  (:require [cljs-node-io.fs :refer [areaddir areadFile awriteFile adir? astat areadFile]]
            [clojure.core.async :refer [go chan >! <!]]
            ["path" :as path]
            [favored-edn.core :refer [write-edn]]
            [clojure.string :as string]
            [chan-utils.core :refer [chan-once all-once]]))

(defn chan-gather-entries [base-dir entries]
  (->> entries
       (filter
        (fn [entry]
          (not
           (contains?
            #{".cache-loader" ".awcache" ".hswcache" "dist" "dll" ".sass-cache" "_scss"}
            entry))))
       (all-once
        (fn [entry]
          (chan-once
           got
           (go
            (let [child (path/join base-dir entry), is-dir? (<! (adir? child))]
              (if is-dir?
                (let [[err children] (<! (areaddir child))
                      results (<! (chan-gather-entries child children))]
                  (got {:name entry, :path child, :children results}))
                (let [[err stat] (<! (astat child))
                      [err content] (<! (areadFile child "utf8"))
                      lines-count (->> (string/split content "\n")
                                       (filter (fn [x] (not (string/blank? x))))
                                       (count))]
                  (got {:name entry, :path child, :size (.-size stat), :lines lines-count}))))))))))

(def picked-entries ["shared" "account" "system" "admin" "fi" "global"])

(defn task! []
  (println "Collect files")
  (go
   (let [info (<! (chan-gather-entries "." picked-entries))]
     (<! (awriteFile (path/join js/__dirname "info.edn") (write-edn info) {}))
     (println "Finished"))))

(defn main! [] (task!))

(defn reload! [] (task!))
