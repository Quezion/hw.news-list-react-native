(ns example.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :get-articles
 (fn [{:keys [articles] :as db} _]
   articles))
