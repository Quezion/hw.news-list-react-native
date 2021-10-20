(ns example.events
  (:require
   [re-frame.core :refer [reg-fx
                          reg-event-db
                          reg-event-fx]]
   [example.db :as db :refer [app-db]]
   ["react-native" :as rn]))

(defn get-react-property
  "Little shim to fetch these at runtime"
  [name]
  (if rn
    (aget rn name)
    #js {}))

(def linking (get-react-property "Linking"))

(reg-event-db
 :initialize-db
 (fn [_ _]
   app-db))

(reg-fx
 :goto-url
 (fn [url]
   (.openURL linking url)))

(reg-event-fx
 :open-url
 (fn [{:keys [db]} [_ url]]
   {:goto-url url}))
