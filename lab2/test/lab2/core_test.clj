(ns lab2.core-test
  (:require
   [clojure.test :refer [deftest]]
   [lab2.preset-impl :refer [size-pre-set conj-pre-set conj-set contains-pre-set?
                             disj-pre-set empty-pre-set filter-pre-set map-pre-set
                             pre-set pre-set-seq pre-set? reduce-pre-set-left
                             reduce-pre-set-right]]
   [lab2.test-macroses :refer [check should-be should-contain
                               should-not-contain should=]]))

(deftest empty-set-test
  (check "Empty set creation and properties"
         (should= 0 (size-pre-set empty-pre-set))
         (should-be (pre-set? empty-pre-set))
         (should-be (empty? (pre-set-seq empty-pre-set)))
         (should-not-contain empty-pre-set "anything")))

(deftest basic-operations-test
  (check "Add elements to set"
         (let [s1 (conj-pre-set empty-pre-set "test")
               s2 (conj-pre-set s1 "hello")]
           (should-contain s1 "test")
           (should-contain s2 "hello")
           (should-contain s2 "test")
           (should-not-contain s1 "hello")))

  (check "Idempotency - adding existing element"
         (let [s1 (pre-set "a" "b")
               s2 (conj-pre-set s1 "a")]
           (should= s1 s2)))

  (check "Remove elements"
         (let [s1 (pre-set "a" "b" "c")
               s2 (disj-pre-set s1 "b")]
           (should-not-contain s2 "b")
           (should-contain s2 "a")
           (should-contain s2 "c")))

  (check "Remove non-existing element"
         (let [s1 (pre-set "a" "b")
               s2 (disj-pre-set s1 "c")]
           (should= s1 s2))))

(deftest sequence-operations-test
  (check "Set sequence"
         (let [s (pre-set "x" "y" "z")
               elements (pre-set-seq s)]
           (should= 3 (count elements))
           (should-be (every? #(contains-pre-set? s %) elements))))

  (check "Filter operation"
         (let [s (pre-set "cat" "banana" "cherry" "bebe")
               filtered (filter-pre-set #(.startsWith % "c") s)]
           (should-contain filtered "cherry")
           (should-not-contain filtered "banana")
           (should= 2 (count (pre-set-seq filtered)))))

  (check "Map operation"
         (let [s (pre-set "a" "b")
               mapped (map-pre-set #(str % "!") s)]
           (should-contain mapped "a!")
           (should-contain mapped "b!")
           (should= 2 (count (pre-set-seq mapped))))))

(deftest reduce-operations-test
  (check "Left reduce"
         (let [s (pre-set "1" "2" "3")]
           (should= "123" (reduce-pre-set-left str s))
           (should= 6 (transduce (map #(Integer/parseInt %)) + (pre-set-seq s)))))

  (check "Right reduce"
         (let [s (pre-set "a" "b" "c")]
           (should= "cba" (reduce-pre-set-right str s)))))

(deftest protocol-interface-test
  (check "Polymorphic interface via PSet protocol"
         (let [s (pre-set "test")]
           (should-contain s "test")
           (should-not-contain s "unknown")

           (let [new-s (conj-set s "new")]
             (should-contain new-s "new")
             (should-contain new-s "test")))))

(deftest prefix-tree-specific-test
  (check "Prefix tree structure with common prefixes"
         (let [s (pre-set "car" "card" "cart" "cat" "dog")]
           (should-contain s "car")
           (should-contain s "card")
           (should-contain s "cart")
           (should-contain s "cat")
           (should-contain s "dog")
           (should-not-contain s "ca")
           (should-not-contain s "cars"))))