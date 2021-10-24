(ns example.app
  (:require [day8.re-frame.http-fx] ;; must be here for app setup
            [example.events]
            [example.subs]
            [example.react :refer [flat-list
                                   text
                                   view
                                   touchable-highlight]]
            [expo.root :as expo-root]
            ["expo-status-bar" :refer [StatusBar]]
            [re-frame.core :as rf]
            ["react-native" :as rn]
            [reagent.core :as r]))

(defn trunc
  "This is unexpectedly missing in clojure.string"
  [s n]
  (subs s 0 (min (count s) n)))

(defn trunc-trailing-dots
  "Forces string to maximum width. Appends with ... if string would be too long"
  [length s]
  (if (neg? (- length (count s)))
    (str (trunc s (- length 3)) "...")
    s))

(defn article-row
  [id title date abstract]
  (-> [:> view {:style {:margin-top 0 :margin-bottom 0
                        :margin-left 0 :margin-right 0
                        :padding-top 0
                        :padding-bottom 0
                        :height (if (seq abstract)
                                  80
                                  50)
                        :border-bottom-width 1
                        :flex-direction "row"
                        :background-color "transparent"}}
       [:> view {:style {:flex-direction "column"
                         :padding-left 2}}
        [:> view {:style {:flex-direction "row"
                          :width "100%"}}
         [:> text {:style {:font-size 12 :font-weight "bold"
                           :text-align "left"
                           :margin-top 0}}
          (trunc-trailing-dots 66 title)]]
        [:> text {:style {:font-size 12
                          :text-align "left"
                          :margin-top 1}}
         (str date)]
        [:> text {:style {:font-size 10
                          :text-align "left"
                          :margin-top 3}}
         (trunc-trailing-dots 160 abstract)]]]
      (with-meta {:key id})))

(defn clickable-wrapper
  [callback child]
  (let [bg-color "transparent"
        on-press callback]
    [:> touchable-highlight {:on-press on-press
                             :underlay-color "white"}
     child]))

(defn render-article
  [{:keys [url title published_date abstract]}]
  (clickable-wrapper #(rf/dispatch [:open-url url])
                     (article-row url title published_date abstract)))

(defn article-display-panel
  [articles]
  [:> view {:style {:flex-direction "column"}}
   [:> view {:style {:height 3}}] ;; simple vertical spacer
   [flat-list {:data articles
               :render-item render-article
               :content-container-style {:justify-content  :center
                                         :flex-direction   :column
                                         :margin-left      0
                                         :margin-vertical  0
                                         :padding-bottom 10}}]])

(defn root []
  (fn []
    [:> view {:style {:flex 1
                      :padding-vertical 50
                      :justify-content :space-between
                      :align-items :center
                      :background-color :white}}
     (let [articles (rf/subscribe [:get-articles])]
       [article-display-panel @articles])
     [:> StatusBar {:style "auto"}]]))

(defn start
  {:dev/after-load true}
  []
  (rf/dispatch [:load-articles])
  (expo-root/render-root (r/as-element [root])))

(defn init []
  (rf/dispatch-sync [:initialize-db])
  (start))
