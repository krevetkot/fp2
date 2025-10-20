(ns lab2.test-property-based
  (:require [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [lab2.preset-impl :refer [->pre-set concat-pre-set conj-pre-set
                                      contains-pre-set? disj-pre-set
                                      empty-pre-set pre-set?]]))

; Генераторы для тестов
(def string-gen
  (s/gen string?))

(def pre-set-gen
  (gen/fmap ->pre-set
            (gen/vector string-gen 0 10)))

(defspec monoid-associativity-property 100
  (prop/for-all [a pre-set-gen
                 b pre-set-gen
                 c pre-set-gen]
                (= (concat-pre-set a (concat-pre-set b c))
                   (concat-pre-set (concat-pre-set a b) c))))

(defspec monoid-identity-property 100
  (prop/for-all [a pre-set-gen]
                (and (= (concat-pre-set empty-pre-set a) a)
                     (= (concat-pre-set a empty-pre-set) a))))

(defspec monoid-closure-property 100
  (prop/for-all [a pre-set-gen
                 b pre-set-gen]
                (pre-set? (concat-pre-set a b))))

(defspec add-idempotency-property 100
  (prop/for-all [s pre-set-gen
                 element string-gen]
                (let [s-with-element (conj-pre-set s element)
                      s-twice (conj-pre-set s-with-element element)]
                  (= s-with-element s-twice))))

(defspec remove-non-existing-property 100
  (prop/for-all [s pre-set-gen
                 element string-gen]
                (let [s-without (disj-pre-set s element)]
                  (if (contains-pre-set? s element)
                    true
                    (= s s-without)))))

(defspec elements-preservation-property 100
  (prop/for-all [elements (gen/vector string-gen 0 5)
                 extra-element string-gen]
                (let [s (->pre-set elements)
                      s-with-extra (conj-pre-set s extra-element)]
                  (every? #(contains-pre-set? s-with-extra %) elements))))

(defspec union-commutativity-property 100
  (prop/for-all [a pre-set-gen
                 b pre-set-gen]
                (= (concat-pre-set a b)
                   (concat-pre-set b a))))