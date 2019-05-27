
(ns app.collector
  (:require [cljs-node-io.fs :refer [areaddir areadFile awriteFile adir? astat areadFile]]
            [clojure.core.async :refer [go chan >! <!]]
            [cumulo-util.async :refer [all-once]]
            ["path" :as path]
            [favored-edn.core :refer [write-edn]]
            [clojure.string :as string]))

(defn chan-gather-entries [base-dir entries]
  (->> entries
       (filter
        (fn [entry]
          (not (contains? #{".cache-loader" ".awcache" ".hswcache" "dist" "dll"} entry))))
       (all-once
        (fn [entry]
          (let [<child-result (chan)]
            (go
             (let [child (path/join base-dir entry), is-dir? (<! (adir? child))]
               (if is-dir?
                 (let [[err children] (<! (areaddir child))
                       results (<! (chan-gather-entries child children))]
                   (>! <child-result {:name entry, :path child, :children results}))
                 (let [[err stat] (<! (astat child))
                       [err content] (<! (areadFile child "utf8"))
                       lines-count (count (string/split content "\n"))]
                   (>!
                    <child-result
                    {:name entry, :path child, :size (.-size stat), :lines lines-count})))))
            <child-result)))))

(def picked-entries ["shared" "account" "system" "admin" "fi" "global"])

(defn task! []
  (println "Collect files")
  (go
   (let [info (<! (chan-gather-entries "." picked-entries))]
     (<! (awriteFile (path/join js/__dirname "info.edn") (write-edn info) {}))
     (println "Finished"))))

(defn main! [] (task!))

(defn reload! [] (task!))
