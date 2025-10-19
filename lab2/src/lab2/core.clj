(ns lab2.core
  (:require [lab2.preset-impl :as ps]))


(defn -main []

;; 1. ???????? ????????
(println "=== 1. Create set ===")

(def empty-set ps/empty-pre-set)
(println "Empty set:" empty-set)

(def single-set (ps/pre-set "hello"))
(println "Set with one element:" single-set)

(def multi-set (ps/pre-set "apple" "banana" "cherry" "date"))
(println "Set with many elements:" multi-set)

(def from-coll (ps/->pre-set ["cat" "dog" "bird" "cat"]))
(println "Set from collection:" from-coll)

;; 2. ?????????? ?????????
(println "\n=== 2. Add elements ===")

(def added-set (ps/conj-pre-set multi-set "berry"))
(println "Added 'berry':" added-set)

;; ????????? ???????? ???????????? ???????
(def same-set (ps/conj-pre-set multi-set "apple"))
(println "Add 'apple' (already have):" same-set)
(println "Is set the same?:" (= multi-set same-set))

;; 3. ???????? ?????????
(println "\n=== 3. Delete elements ===")

(def removed-set (ps/disj-pre-set multi-set "banana"))
(println "Delete 'banana':" removed-set)

;; ????????? ??????? ?????????????? ???????
(def not-removed (ps/disj-pre-set multi-set "bebebe"))
(println "Try to delete 'bebebe':" not-removed)
(println "Is set the same?:" (= multi-set not-removed))

;; 4. ???????? ??????????????
(println "\n=== 4. Contains ===")

(println "Contains 'apple'?" (ps/contains-pre-set? multi-set "apple"))
(println "Contains 'zebra'?" (ps/contains-pre-set? multi-set "zebra"))
(println "Contains 'banana'?" (ps/contains-pre-set? multi-set "banana"))

;; 5. ????????? ??????????????????
(println "\n=== 5. Seq ===")

(def elements (ps/pre-set-seq multi-set))
(println "All elements as seq:" elements)
(println "Type seq:" (type elements))

;; 6. ??????? ??????? ???????
(println "\n=== 6. High level functions ===")

;; ??????????
(def filtered (ps/filter-pre-set #(.startsWith % "c") multi-set))
(println "Elements with 'c':" (ps/pre-set-seq filtered))

;; ???????????
(def mapped (ps/map-pre-set #(str % "!") multi-set))
(println "Elements with '!':" (ps/pre-set-seq mapped))

;; ???????
(def concatenated (ps/reduce-pre-set-left str multi-set))
(println "Concat:" concatenated)

(def sum-lengths (ps/reduce-pre-set-left #(+ %1 (count %2)) 0 multi-set))
(println "Sum of all length:" sum-lengths)

;; 7. ??????????? ????????? (???????? PSet)
(println "\n=== 7. Poly interface ===")

(def protocol-set (ps/conj-set multi-set "fig"))
(println "Add by protocol:" (ps/set-seq protocol-set))

(println "Check by protocol (contains 'apple'):"
         (ps/contains-set? multi-set "apple"))

;; 8. ???????? ?? ???????? ???????
(println "\n=== 8. Monoid characteristics ===")
(def A (ps/pre-set "a" "b"))
(def B (ps/pre-set "c" "d"))
(def C (ps/pre-set "e" "f"))

(def left (ps/concat-pre-set A (ps/concat-pre-set B C)))
(def right (ps/concat-pre-set (ps/concat-pre-set A B) C))

(println "Assoativnost:" (= left right))

(println "Neutral element existance {} + A:" (= (ps/concat-pre-set ps/empty-pre-set A) A))
(println "Neutral element existance A + {}:" (= (ps/concat-pre-set A ps/empty-pre-set) A))

(println "Closure:" (ps/pre-set? (ps/concat-pre-set A B)))

;; 11. ?????? ? ?????? (????????)
(println "\n=== 9. Prefixes ===")

(def prefix-set (ps/pre-set "car" "card" "cart" "cat" "dog"))
(println "Set with the same prefixes:" (ps/pre-set-seq prefix-set))

;; ????????, ??? ??? ???????? ?????????? ?????????
(println "Contains 'car'?" (ps/contains-pre-set? prefix-set "car"))
(println "Contains 'card'?" (ps/contains-pre-set? prefix-set "card"))
(println "Contains 'cart'?" (ps/contains-pre-set? prefix-set "cart"))
(println "Contains 'cat'?" (ps/contains-pre-set? prefix-set "cat"))
(println "Contains 'dog'?" (ps/contains-pre-set? prefix-set "dog"))
)