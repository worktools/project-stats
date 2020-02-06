
(ns app.frequency
  (:require ["child_process" :as cp]
            ["fs" :as fs]
            [clojure.string :as string]
            [clojure.core.async :refer [chan go go-loop <! >! close! timeout]])
  (:require-macros [clojure.core.strint :refer [<<]]))

(defn add-to [acc xs] (if (empty? xs) acc (recur (conj acc (first xs)) (rest xs))))

(defn chan-get-files [commit beginning]
  (let [<ret (chan)]
    (cp/exec
     (<< "git diff-tree -r --no-commit-id --name-only --diff-filter=ACMRT ~{commit}")
     (fn [error stdout stderr]
       (if (some? error) (js/console.error error))
       (go
        (>!
         <ret
         (->> (string/split stdout "\n")
              (filter
               (fn [x]
                 (if (string/blank? x)
                   false
                   (if (some? beginning) (string/starts-with? x beginning) true)))))))))
    <ret))

(defn main! []
  (let [commits (string/split (cp/execSync "git log --pretty=format:\"%H\"") "\n")
        <source (chan)
        <result (chan)
        concurency 40
        take-size 100000
        beginning (aget js/process.argv 2)]
    (println "Commits count:" (count commits))
    (go-loop
     [check-size (count (take take-size commits)) xs (take take-size commits)]
     (if (empty? xs)
       (close! <source)
       (do
        (>! <source (first xs))
        (comment println "Commits size" (count xs))
        (let [stepped (< (count xs) (- check-size 140))]
          (if stepped (println "Remaing commits:" (count xs)))
          (recur (if stepped (count xs) check-size) (rest xs))))))
    (doseq [i (range concurency)]
      (go-loop
       []
       (let [commit (<! <source)]
         (comment println "handling" i commit)
         (if (nil? commit)
           (let [waited (<! (timeout 100))] (comment println "waited") (close! <result))
           (let [x (<! (chan-get-files commit beginning))] (>! <result x) (recur))))))
    (go-loop
     [acc []]
     (let [x (<! <result)]
       (if (some? x)
         (recur (add-to acc x) )
         (let [count-result (->> (frequencies acc)
                                 (sort-by (fn [[k v]] (unchecked-negate v)))
                                 (map (fn [[k v]] (str v " " k)))
                                 (string/join "\n"))]
           (println count-result)))))))
