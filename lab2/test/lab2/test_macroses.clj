(ns lab2.test-macroses
  (:require [clojure.test :refer [is testing]]
            [lab2.preset-impl :as ps]))

(defmacro check [description & assertions]
  `(testing ~description
     ~@assertions))

(defmacro should= [expected actual]
  `(is (= ~expected ~actual)))

(defmacro should-be [value]
  `(is ~value))

(defmacro should-contain [set element]
  `(is (ps/contains-pre-set? ~set ~element)))

(defmacro should-not-contain [set element]
  `(is (not (ps/contains-pre-set? ~set ~element))))