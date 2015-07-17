;; enumerator.clj
;; define Enumerator type and accompanying instructions
;; Bill Tozier, bill@vagueinnovation.com, 2015

(ns clojush.instructions.enumerator
  (:require [clojush.types.enumerator :as enum])
  (:use [clojush.pushstate]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Instructions for Enumerators
;;
;; Rules of thumb:
;; 
;; There are (and should be) explicit instructions for converting each of the Push collection
;; types into Enumerators, but there is only one Enumerator type; the type of the enclosed
;; seq is maintained for its lifetime.
;; 
;; There are _not_ (and shouldn't be) any instructions which modify the _contents_ of the Push
;; collection inside an Enumerator; treat it as private and immutable for the lifetime of the
;; instance. The only state changes that should ever happen are changes in the pointer value
;; and the loop? state.
;;
;; No Enumerator instance should be made from an empty collection. Any instruction that encounters
;; such an 'empty' instance should destroy it rather than returning results. This includes `unwrap`.


(defn contains-at-least?
  "Returns true only when the number of items in all of the
  indicated prerequisite stacks meets or exceeds the number in state"
  [state & {:as prerequisites}]
  (reduce-kv 
    (fn [satisfied type requirement] 
      (and 
        satisfied 
        (>= (count (type state)) requirement)))
    true
    prerequisites)
  )


(define-registered
  enumerator_from_vector_integer
  ^{:stack-types [:vector_integer :enumerator]}
  (fn [state]
    (if (contains-at-least? state :vector_integer 1)
      (let [collection (first (:vector_integer state))
            popped-state (pop-item :vector_integer state)]
        (if (not (empty? collection))
          (push-item (enum/new-enumerator collection)
          :enumerator
          popped-state)))
      state)))


(define-registered
  enumerator_unwrap
  ^{:stack-types [:enumerator :exec]}
  (fn [state]
    (if (contains-at-least? state :enumerator 1)
      (let [old-seq (:collection (top-item :enumerator state))
            popped-state (pop-item :enumerator state)]
        (if (not (empty? old-seq))
          (push-item  old-seq :exec popped-state)))
    state)))


(define-registered
  enumerator_rewind
  ^{:stack-types [:enumerator]}
  (fn [state]
    (if (contains-at-least? state :enumerator 1)
      (let [old-seq (:collection (top-item :enumerator state))
            popped-state (pop-item :enumerator state)]
        (if (not (empty? old-seq))
          (push-item (enum/new-enumerator old-seq) :enumerator popped-state)))
    state)))


(define-registered
  enumerator_ff
  ^{:stack-types [:enumerator :exec]}
  (fn [state]
    (if (contains-at-least? state :enumerator 1)
      (let [old-seq (:collection (top-item :enumerator state))
            popped-state (pop-item :enumerator state)]
        (if (not (empty? old-seq))
          (push-item (enum/construct-enumerator old-seq (dec (count old-seq))) :enumerator popped-state)))
    state)))


(define-registered
  enumerator_first
  ^{:stack-types [:enumerator :exec]}
  (fn [state]
    (if (contains-at-least? state :enumerator 1)
      (let [old-seq (:collection (top-item :enumerator state))
            popped-state (pop-item :enumerator state)]
        (if (not (empty? old-seq))
          (push-item 
            (enum/new-enumerator old-seq)
            :enumerator
            (push-item 
              (first old-seq)
              :exec
              popped-state))))
      state)))


(define-registered
  enumerator_last
  ^{:stack-types [:enumerator :exec]}
  (fn [state]
    (if (contains-at-least? state :enumerator 1)
      (let [old-seq (:collection (top-item :enumerator state))
            popped-state (pop-item :enumerator state)]
        (if (not (empty? old-seq))
          (push-item 
            (enum/construct-enumerator old-seq (dec (count old-seq)))
            :enumerator
            (push-item 
              (last old-seq)
              :exec
              popped-state))))
      state)))


(define-registered
  enumerator_forward
  ^{:stack-types [:enumerator]}
  (fn [state]
    (if (contains-at-least? state :enumerator 1)
      (let [old-state (top-item :enumerator state)
            old-seq (:collection old-state)
            old-ptr (:pointer old-state)
            done (>= (inc old-ptr) (count old-seq))
            popped-state (pop-item :enumerator state)]
        (if (and (not (empty? old-seq)) (not done))
          (push-item (enum/construct-enumerator old-seq (inc old-ptr)) :enumerator popped-state)))
      state)))


(define-registered
  enumerator_backward
  ^{:stack-types [:enumerator]}
  (fn [state]
    (if (contains-at-least? state :enumerator 1)
      (let [old-state (top-item :enumerator state)
            old-seq (:collection old-state)
            old-ptr (:pointer old-state)
            done (neg? (dec old-ptr))
            popped-state (pop-item :enumerator state)]
        (if (and (not (empty? old-seq)) (not done))
          (push-item (enum/construct-enumerator old-seq (dec old-ptr)) :enumerator popped-state)))
    state)))


(define-registered
  enumerator_next
  ^{:stack-types [:enumerator :exec]}
  (fn [state]
    (if (contains-at-least? state :enumerator 1)
      (let [old-state (top-item :enumerator state)
            old-seq (:collection old-state)
            old-ptr (:pointer old-state)
            done (>= (inc old-ptr) (count old-seq))
            popped-state (pop-item :enumerator state)]
        (if (not (empty? old-seq))
          (let [state-with-item (push-item (nth old-seq old-ptr) :exec popped-state)]
            (if (not done)
              (push-item (enum/construct-enumerator old-seq (inc old-ptr)) :enumerator state-with-item)
              state-with-item))))
    state)))

(define-registered
  enumerator_prev
  ^{:stack-types [:enumerator :exec]}
  (fn [state]
    (if (contains-at-least? state :enumerator 1)
      (let [old-state (top-item :enumerator state)
            old-seq (:collection old-state)
            old-ptr (:pointer old-state)
            done (< (dec old-ptr) 0)
            popped-state (pop-item :enumerator state)]
        (if (not (empty? old-seq))
          (let [state-with-item (push-item (nth old-seq old-ptr) :exec popped-state)]
            (if (not done)
              (push-item (enum/construct-enumerator old-seq (dec old-ptr)) :enumerator state-with-item)
              state-with-item))))
    state)))
