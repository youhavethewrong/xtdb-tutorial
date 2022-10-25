(ns xtdb-tutorial.core
  (:require [xtdb.api :as xt]))

;; My essential info
(def manifest
  {:xt/id :manifest
   :pilot-name "Johanna"
   :id/rocket "SB002-sol"
   :id/employee "22910x2"
   :badges ["SETUP" "PUT" "DATALOG-QUERIES"]
   :cargo ["stereo" "gold fish" "slippers" "secret note"]})

;; the XTDB standalone in-memory node
(def node (xt/start-node {}))

;; get my manifest synced in
(xt/submit-tx node [[::xt/put manifest]])
(xt/sync node)

(defn easy-ingest
  "Uses XTDB put transaction to add a vector of documents to a specified
  node"
  [node docs]
  (xt/submit-tx node
                (vec (for [doc docs]
                       [::xt/put doc])))

  (xt/sync node))

(def commodity-data
  [{:xt/id :commodity/Pu
    :common-name "Plutonium"
    :type :element/metal
    :density 19.816
    :radioactive true}

   {:xt/id :commodity/N
    :common-name "Nitrogen"
    :type :element/gas
    :density 1.2506
    :radioactive false}

   {:xt/id :commodity/CH4
    :common-name "Methane"
    :type :molecule/gas
    :density 0.717
    :radioactive false}

   {:xt/id :commodity/Au
    :common-name "Gold"
    :type :element/metal
    :density 19.300
    :radioactive false}

   {:xt/id :commodity/C
    :common-name "Carbon"
    :type :element/non-metal
    :density 2.267
    :radioactive false}

   {:xt/id :commodity/borax
    :common-name "Borax"
    :IUPAC-name "Sodium tetraborate decahydrate"
    :other-names ["Borax decahydrate" "sodium borate" "sodium tetraborate" "disodium tetraborate"]
    :type :mineral/solid
    :appearance "white solid"
    :density 1.73
    :radioactive false}])

(easy-ingest node commodity-data)

(defn filter-type
  [type]
  (xt/q (xt/db node)
        '{:find [name]
          :where [[e :common-name name]
                  [e :type type]]
          :in [type]}
        type))

(defn filter-appearance
  [description]
  (xt/q (xt/db node)
        '{:find [name IUPAC]
          :where [[e :common-name name]
                  [e :IUPAC-name IUPAC]
                  [e :appearance appearance]]
          :in [appearance]}
        description))

;; (xt/submit-tx node [[::xt/put manifest]])
;; (xt/sync node)
;; (xt/entity (xt/db node) :manifest)

;; (xt/submit-tx
;;  node
;;  [[::xt/put
;;    {:xt/id :commodity/Pu
;;     :common-name "Plutonium"
;;     :type :element/metal
;;     :density 19.816
;;     :radioactive true}]

;;   [::xt/put
;;    {:xt/id :commodity/N
;;     :common-name "Nitrogen"
;;     :type :element/gas
;;     :density 1.2506
;;     :radioactive false}]

;;   [::xt/put
;;    {:xt/id :commodity/CH4
;;     :common-name "Methane"
;;     :type :molecule/gas
;;     :density 0.717
;;     :radioactive false}]])

;; (xt/submit-tx
;;  node
;;  [[::xt/put
;;    {:xt/id :stock/Pu
;;     :commod :commodity/Pu
;;     :weight-ton 21 }
;;    #inst "2115-02-13T18"]

;;   [::xt/put
;;    {:xt/id :stock/Pu
;;     :commod :commodity/Pu
;;     :weight-ton 23 }
;;    #inst "2115-02-14T18"]

;;   [::xt/put
;;    {:xt/id :stock/Pu
;;     :commod :commodity/Pu
;;     :weight-ton 22.2 }
;;    #inst "2115-02-15T18"]

;;   [::xt/put
;;    {:xt/id :stock/Pu
;;     :commod :commodity/Pu
;;     :weight-ton 24 }
;;    #inst "2115-02-18T18"]

;;   [::xt/put
;;    {:xt/id :stock/Pu
;;     :commod :commodity/Pu
;;     :weight-ton 24.9 }
;;    #inst "2115-02-19T18"]])

;; (xt/submit-tx
;;  node
;;  [[::xt/put
;;    {:xt/id :stock/N
;;     :commod :commodity/N
;;     :weight-ton 3 }
;;    #inst "2115-02-13T18"
;;    #inst "2115-02-19T18"]

;;   [::xt/put
;;    {:xt/id :stock/CH4
;;     :commod :commodity/CH4
;;     :weight-ton 92 }
;;    #inst "2115-02-15T18"
;;    #inst "2115-02-19T18"]])

;; (xt/entity (xt/db node #inst "2115-02-14") :stock/Pu)

;; (xt/entity (xt/db node #inst "2115-02-18") :stock/Pu)

;; (xt/submit-tx
;;  node
;;  [[::xt/put
;;    {:xt/id :manifest
;;     :pilot-name "Johanna"
;;     :id/rocket "SB002-sol"
;;     :id/employee "22910x2"
;;     :badges ["SETUP" "PUT"]
;;     :cargo ["stereo" "gold fish" "slippers" "secret note"]}]])

;; -- finding by properties
;; (xt/q (xt/db node) '{:find [element] :where [[element :type :element/metal]]})

;; -- Quote the query any way I want
;; (=
;;  (xt/q (xt/db node)
;;          '{:find [element]
;;            :where [[element :type :element/metal]]})

;;  (xt/q (xt/db node)
;;          {:find '[element]
;;           :where '[[element :type :element/metal]]})

;;  (xt/q (xt/db node)
;;          (quote
;;           {:find [element]
;;            :where [[element :type :element/metal]]})))

;; -- finding and mapping
;; (xt/q (xt/db node)
;;         '{:find [name]
;;           :where [[e :type :element/metal]
;;                   [e :common-name name]]})
;; -- return multiple properties
;; (xt/q (xt/db node)
;;         '{:find [name rho]
;;           :where [[e :density rho]
;;                   [e :common-name name]]})
;; -- arguments
;; (xt/q (xt/db node)
;;       '{:find [name]
;;         :where
;;         [[e :type type]
;;          [e :common-name name]]
;;         :in [type]}
;;       :element/metal)


;; Stopping at https://nextjournal.com/xtdb-tutorial/datalog#Spaceport for now
