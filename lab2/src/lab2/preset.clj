(ns lab2.preset)

; Узел префиксного дерева
(defrecord TrieNode [children end-of-word?])

; Множество
(defrecord PreSet [root])

; Создание пустого множества
(def empty-pre-set (->PreSet (->TrieNode {} false)))


; ===================================== Вспомогательные приватные функции =====================================
(defn- new-node
  ([] (->TrieNode {} false))
  ([end?] (->TrieNode {} end?)))

(defn- new-leaf-node [] (new-node true))

(defn- string-to-chars [s]
  (seq (str s)))

(defn- get-child [node char]
  (get (:children node) char))

(defn- update-children [node char new-child]
  (assoc node :children (assoc (:children node) char new-child)))

(defn- remove-child [node char]
  (assoc node :children (dissoc (:children node) char)))

; ===================================== Основные функции API set =====================================
; ========== add ==========
(defn- add-by-char [node chars]
  (if (empty? chars)
    (assoc node :end-of-word? true)
    (let [current-char (first chars)
          remaining-chars (rest chars)
          possible-child (get-child node current-char)
          child (if (empty? possible-child) (new-node) possible-child)]
      
      (update-children node
                       current-char
                       (add-by-char child remaining-chars)))))

(defn add-element [pre-set element]
  (->PreSet (add-by-char (:root pre-set) (string-to-chars element))))

; ========== delete ==========
(defn- delete-by-char [node chars]
  (if (empty? chars)
    ;; Дошли до конца - снимаем флаг конечного узла
    (if (empty? (:children node))
      nil ; Удаляем узел, если нет детей
      (assoc node :end-of-word? false))

    (let [current-char (first chars)
          remaining-chars (rest chars)
          child (get-child node current-char)]

      (if (nil? child)
        node ; Элемент не найден - возвращаем как есть
        (let [updated-child (delete-by-char child remaining-chars)]
          (if (nil? updated-child)
            (remove-child node current-char)
            (update-children node current-char updated-child)))))))

(defn delete-element [pre-set element]
  (let [new-root (delete-by-char (:root pre-set) (string-to-chars element))]
    (if (nil? new-root)
      empty-pre-set
      (->PreSet new-root))))

; ========== contains ==========
(defn- contains-by-char? [node chars]
  (cond
    (nil? node) false
    (empty? chars) (:end-of-word? node)
    :else (let [current-char (first chars)
                remaining-chars (rest chars)
                child (get-child node current-char)]
            (contains-by-char? child remaining-chars))))

(defn contains-pre-set? [pre-set element]
  (contains-by-char? (:root pre-set) (string-to-chars element)))

; ========== size ==========
(defn- count-impl [node]
  (let [current-count (if (:end-of-word? node) 1 0)
        children-count (reduce + (map (fn [[_ child]] (count-impl child))
                                      (:children node)))]
    (+ current-count children-count)))

(defn size-pre-set [pre-set]
  (count-impl (:root pre-set)))