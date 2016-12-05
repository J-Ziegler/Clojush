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
  [input]
  (push-item input :input (make-push-state)))

(defn actual-output
  [program inputs]
  (let [start-state (make-start-state inputs)
        end-state (run-push program start-state)
        top-int (top-item :integer end-state)]
    top-int))

(defn abs [n]
  (if (< n 0)
    (- n)
    n))

(defn all-errors
  [program]
  (doall
    (for [inputs input-set]
      (let [expected (expected-output inputs)
            actual (actual-output program inputs)]
        (if (= actual :no-stack-item)
          1000
          (abs (- expected actual)))))))

(def atom-generators
  (concat (registered-for-stacks [:integer :boolean :exec])
          (list (fn [] (lrand-int 100)) 'in1 3 5)))

(def argmap
  {:error-function all-errors
   :atom-generators atom-generators
   })
