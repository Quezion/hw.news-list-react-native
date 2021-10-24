(ns example.events
  (:require
   [re-frame.core :refer [reg-fx
                          reg-event-db
                          reg-event-fx]]
   [example.db :as db :refer [app-db]]
   ["react-native" :as rn]
   [example.react :refer [linking]]
   [ajax.core :as ajax]))

(reg-event-db
 :initialize-db
 (fn [_ _]
   app-db))

;; The isolation of 'effect' from 'event' here is a re-frame tricked to improve testability
;; It would also allow for more complex app functions -- like transacting to the database
;; AND navigating to URL. This makes it easy to do things like "maintain history of visited URLs"
(reg-fx
 :goto-url
 (fn [url]
   (.openURL linking url)))

(reg-event-fx
 :open-url
 (fn [{:keys [db]} [_ url]]
   {:goto-url url}))

;; For this small repo, these are my personal credentials -- no misusing! :)
(def credentials
  {:api-key "Ls0iGCzqn0WCVGcykutnFWPAsHosrpva"
   :password "BHAANknfG9cp72pL"
   :app-id "b5a23886-0358-4c4f-9532-0f347bde03ba"})

(def base-uri "https://api.nytimes.com/svc/news/v3")

(defn newswire-uri
  [source section]
  (str base-uri "/content/" (name source) "/" (name section) ".json"))

(def article-keys
  [:section
   :subsection
   :title
   :abstract
   :url
   ;;:short_url
   :byline
   :thumbnail_standard
   :item_type
   :source
   ;;:updated_date
   ;;:created_date
   :published_date
   ;;:material_type_facet
   ;;:kicker
   :headline
   ;;:des_dacet
   ;;:org_facet
   ;;:per_facet
   ;;:geo_facet
   ;;:blog_name
   ;;:related_urls
   :multimedia])

(reg-event-fx
 :http-no-on-failure
 (fn [{:keys [db]} something]
   (println "UNHANDLED HTTP REQUEST FAILURE! see event :http-no-on-failure")
   (println something)))

(reg-event-fx
 :load-articles-succeeded
 (fn [{:keys [db]} [_ response]]
   ;;(println "load succeeded, response below")
   ;;(println response)
   (let [articles (-> response
                      (js->clj {:keywordize-keys true})
                      :results)]
     {:db (-> (update db :articles into articles)
              (assoc :loading? false))})))

(reg-event-fx
 :load-articles-failed
 (fn [{:keys [db]} response]
   (println "load failed, response below")
   (println response)
   {:db (update db :loading? false)}))

(reg-event-fx
 :load-articles
 (fn [{:keys [db]} [_ _]]
   (println "load event fired")
   {:db (assoc db :loading? true)
    :http-xhrio {:method :get
                 :uri (str (newswire-uri :all :all) ) ;; "/?" "api-key=" (:api-key credentials)
                 :params {:api-key (:api-key credentials)}
                 :timeout 5000
                 :format (ajax/text-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:load-articles-succeeded]
                 :on-failure [:load-articles-failed]}}))
