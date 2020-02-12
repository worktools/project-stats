
(ns app.file-size
  (:require ["path" :as path]
            ["fs" :as fs]
            ["chalk" :as chalk]
            [app.util.file :refer [get-dir-files!]]
            [clojure.string :as string])
  (:require-macros [clojure.core.strint :refer [<<]]))

(def preference {:limit-size (or js/process.env.limitSize 500)})

(defn main! []
  (let [all-files (get-dir-files! ".")]
    (doseq [filepath all-files]
      (let [content (fs/readFileSync filepath "utf8")
            lines (string/split content "\n")
            code-lines (->> lines (filter (fn [line] (not (string/blank? line)))))]
        (when (> (count code-lines) (:limit-size preference))
          (println)
          (println (chalk/bold (chalk/white filepath)))
          (println (count code-lines) "lines of code in" (count lines) "lines."))))
    (println "Finished script.")))
