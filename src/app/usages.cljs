
(ns app.usages
  (:require ["path" :as path]
            ["fs" :as fs]
            ["chalk" :as chalk]
            [app.util.file :refer [get-dir-files!]]
            [clojure.string :as string])
  (:require-macros [clojure.core.strint :refer [<<]]))

(def preference {:queries ["useState(" "useState<"], :highlight "useState"})

(defn main! []
  (let [all-files (get-dir-files! ".")]
    (doseq [filepath all-files]
      (let [content (fs/readFileSync filepath "utf8")
            lines (string/split content "\n")
            lines-with-it (->> lines
                               (filter
                                (fn [line]
                                  (some
                                   (fn [query] (string/includes? line query))
                                   (:queries preference)))))]
        (when-not (< (count lines-with-it) 9)
          (println)
          (println
           (chalk/bold (chalk/white filepath))
           (let [size (count lines-with-it)] (<< "with ~{size} usages.")))
          (doseq [line lines-with-it]
            (println
             (chalk/gray
              (string/replace
               (string/trim line)
               (:highlight preference)
               (fn [code] (chalk/yellow code)))))))))
    (println "Finished script.")))
