(ns lab2.preset-impl
  (:require [clojure.string :as string]))

;; ===================================== ТИПЫ ДАННЫХ И ПРОТОКОЛ =====================================
(defrecord TrieNode [children end-of-word?])
(defrecord PreSet [root])

(defprotocol PSet
  "Протокол для функциональных множеств."
  (conj-set [this element])
  (disj-set [this element])
  (contains-set? [this element])
  (set-seq [this]))

(def empty-pre-set (->PreSet (->TrieNode {} false)))

; ===================================== Вспомогательные приватные функции =====================================
(defn- new-node
  ([] (->TrieNode {} false))
  ([end?] (->TrieNode {} end?)))

(defn- string-to-chars [s]
  (seq (str s)))

(defn- get-child [node ch]
  (get (:children node) ch))

(defn- update-children [node ch new-child]
  (assoc-in node [:children ch] new-child))

(defn- remove-child [node ch]
  (update-in node [:children] dissoc ch))

; ===================================== Основные функции API set =====================================
; ========== add ==========
(defn- conj-impl [node chs]
  (if (empty? chs)
    (assoc node :end-of-word? true)
    (let [current-char (first chs)
          remaining-chars (rest chs)
          possible-child (get-child node current-char)
          child (if (empty? possible-child) (new-node) possible-child)]

      (update-children node
                       current-char
                       (conj-impl child remaining-chars)))))

(defn conj-pre-set [pre-set element]
  (->PreSet (conj-impl (:root pre-set) (string-to-chars element))))

; ========== delete ==========
(defn- disj-impl [node chs]
  (if (empty? chs)
    ;; Дошли до конца - снимаем флаг конечного узла
    (when (seq (:children node)) (assoc node :end-of-word? false))

    (let [current-char (first chs)
          remaining-chars (rest chs)
          child (get-child node current-char)]

      (if (nil? child)
        node ; Элемент не найден - возвращаем как есть
        (let [updated-child (disj-impl child remaining-chars)]
          (if (nil? updated-child)
            (remove-child node current-char)
            (update-children node current-char updated-child)))))))

(defn disj-pre-set [pre-set element]
  (let [new-root (disj-impl (:root pre-set) (string-to-chars element))]
    (if (nil? new-root)
      empty-pre-set
      (->PreSet new-root))))

; ========== contains ==========
(defn- contains-by-char? [node chs]
  (cond
    (nil? node) false
    (empty? chs) (:end-of-word? node)
    :else (let [current-char (first chs)
                remaining-chars (rest chs)
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

; ========== обход дерева для map & reduce ==========
(defn- traverse [node current-path]
  (lazy-seq
   (let [current-result (when (:end-of-word? node) (string/join current-path))
         child-results (mapcat (fn [[ch child]]
                                 (traverse child (conj current-path ch)))
                               (:children node))]
     (if current-result
       (cons current-result child-results)
       child-results))))

(defn pre-set-seq [pre-set]
  (traverse (:root pre-set) []))

; ========== фильтрация ==========
(defn filter-pre-set [pred pre-set]
  (->> (pre-set-seq pre-set)
       (filter pred)
       (reduce conj-pre-set empty-pre-set)))

; ========== отображение ==========
(defn map-pre-set [f pre-set]
  (->> (pre-set-seq pre-set)
       (map f)
       (reduce conj-pre-set empty-pre-set)))

; ========== свертки ==========
; left
(defn reduce-pre-set-left
  ([f pre-set] (reduce-pre-set-left f (f) pre-set))
  ([f init pre-set]
   (reduce f init (pre-set-seq pre-set))))

; right
(defn reduce-pre-set-right
  ([f pre-set] (reduce-pre-set-right f (f) pre-set))
  ([f init pre-set]
   (reduce f init (rseq (vec (pre-set-seq pre-set))))))

; ========== объединение множеств ==========
(defn- union-impl [node1 node2]
  (let [merged-end? (or (:end-of-word? node1) (:end-of-word? node2))
        ;; Объединяем детей через merge-with
        merged-children (merge-with union-impl
                                    (:children node1)
                                    (:children node2))]
    (->TrieNode merged-children merged-end?)))

(defn concat-pre-set [pre-set1 pre-set2]
  (->PreSet (union-impl (:root pre-set1) (:root pre-set2))))

; Нейтральный элемент
(def identity-pre-set empty-pre-set)

;; Реализация протокола для PreSet
(extend-type PreSet
  PSet
  (conj-set [this element] (conj-pre-set this element))
  (disj-set [this element] (disj-pre-set this element))
  (contains-set? [this element] (contains-pre-set? this element))
  (set-seq [this] (pre-set-seq this)))

(defn ->pre-set [coll] (reduce conj-pre-set empty-pre-set coll))
(defn pre-set [& elements] (->pre-set elements))
(defn pre-set? [x] (instance? PreSet x))