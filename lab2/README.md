# Лабораторная работа №2
---
**Выполнила:** Слонимская Ксения Григорьевна  
**Группа:** Р3331  
**Преподаватель:** Пенской Александр Владимирович  
**Язык:** Clojure

---

### Задача
**Реализовать структуру данных префиксное дерево с интерфейсом множества (pre-set)**

---

## Ключевые элементы реализации

Протокол множества:
```
(defprotocol PSet
  (conj-set [this element])
  (disj-set [this element])
  (contains-set? [this element])
  (set-seq [this]))
```

Реализация протокола на основе префиксного дерева:
```
(extend-type PreSet
  PSet
  (conj-set [this element] (conj-pre-set this element))
  (disj-set [this element] (disj-pre-set this element))
  (contains-set? [this element] (contains-pre-set? this element))
  (set-seq [this] (pre-set-seq this)))
```
Само префиксное дерево реализовано таким образом: у каждого узла есть словарь детей и флаг конца слова.
```
(defrecord TrieNode [children end-of-word?])
(defrecord PreSet [root])
```

Структура данных иммутабельна, является моноидом и полиморфна. Эти свойства проверены в тестах.

## Основные реализованные функции

- conj-pre-set [pre-set element] - добавляет элемент в множество. если элемент уже есть, множество не изменяется. если нет - возвращается новое.
- defn disj-pre-set [pre-set element] - по такому же принципу удаляет элемент из множества.
- contains-pre-set? [pre-set element] - проверяет, есть ли элемент в множестве.
- size-pre-set [pre-set] - размер множества.
- pre-set-seq [pre-set] - обход множества (для map, reduce, filter).
- filter-pre-set [pred pre-set] - фильтрация.
- map-pre-set [f pre-set] - отображение.
- defn reduce-pre-set-left & reduce-pre-set-right - свертки.
- union-impl [node1 node2] - объединение множеств.

Нейтральным элементом является empty-pre-set - пустое множество.

## Тестирование

В файле core_test находятся юнит тесты, в файле test_property_based - property-based тесты.
Из-за обилия тестов и желания сделать их более читаемыми (и просто из интереса), я сделала макросы, которые находятся в папке test_macroses.

Вывод тестов:

> --- property-based (clojure.test) ---------------------------  
lab2.test-property-based  
  elements-preservation-property  
  add-idempotency-property  
  remove-non-existing-property  
  monoid-identity-property  
  monoid-closure-property  
  monoid-associativity-property  
  union-commutativity-property  
--- unit (clojure.test) ---------------------------  
lab2.core-test  
  reduce-operations-test  
    Left reduce  
    Right reduce  
  prefix-tree-specific-test  
    Prefix tree structure with common prefixes  
  basic-operations-test  
    Add elements to set  
    Idempotency - adding existing element  
    Remove elements  
    Remove non-existing element  
  sequence-operations-test  
    Set sequence  
    Filter operation  
    Map operation  
  protocol-interface-test  
    Polymorphic interface via PSet protocol  
  empty-set-test  
    Empty set creation and properties   

## Выводы

В данной лабораторной работе я познакомилась с пользовательскими типами данных и полиморфизмом в языке Clojure. Мне удалось отделить реализацию префиксного дерева от интерфеса множества. 
Также я узнала о возможностях property-based тестирования, реализовала его, а также unit тестирование, и перешла на более удобный раннер тестов - kaocha. Еще я хотела для написания тестов использовать midje, но у меня не получилось совместить из с kaocha. Я хочу попробовать доделать это к следующей лабе.
