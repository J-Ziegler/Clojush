;; fizzbuzz.clj

(ns clojush.problems.ec-ai-demos.fizzbuzz
  (:use [clojush.pushgp.pushgp]
        [clojush.random]
        [clojush pushstate interpreter]
        clojush.instructions.common))

;; The fizzbuzz problem.
;; We give the program one integer input.
;; We read the output from the :integer stack
;; 0 - nothing (multiple of neither 3 nor 5)
;; 1 - fizz (multiple of 3)
;; 2 - buzz (multiple of 5)
;; 3 - fizzbuzz (multiple of 3 and 5)

(def input-set
  (vec (range 1 61)))

(defn expected-output
  [input]
  (cond
    (and (= (mod input 3) 0) (= (mod input 5) 0))     3
    (= (mod input 5) 0)                               2
    (= (mod input 3) 0)                               1
    :else                                             0))

(defn make-start-state
  [inputs]
  (reduce (fn [state input]
            (push-item input :input state))
          (make-push-state)
          inputs))
