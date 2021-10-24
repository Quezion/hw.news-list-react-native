(ns example.app
  (:require [day8.re-frame.http-fx] ;; must be here for app setup
            [example.events]
            [example.subs]
            [example.widgets :refer [button]]
            [example.react :refer [flat-list
                                   text
                                   view
                                   touchable-highlight]]
            [expo.root :as expo-root]
            ["expo-status-bar" :refer [StatusBar]]
            [re-frame.core :as rf]
            ["react-native" :as rn]
            [reagent.core :as r]))

(defn article-row
  [id title]
  (-> [:> view {:style {:margin-top 0 :margin-bottom 0
                        :margin-left 0 :margin-right 0
                        :padding-top 0
                        :padding-bottom 0
                        :height 60
                        :border-bottom-width 1
                        :flex-direction "row"
                        :background-color "transparent"}}
       [:> view {:style {:flex-direction "column"
                         :padding-left 2}}
        [:> view {:style {:flex-direction "row"
                          :flex 1
                          :width "100%"}}
         [:> text {:number-of-lines 1
                   :style {:font-size 12 :font-weight "bold"
                           :text-align "left"
                           :margin-top -2}}
          title]]]]
      (with-meta {:key id})))

(defn clickable-wrapper
  [callback child]
  (let [bg-color "transparent"
        on-press callback]
    [:> touchable-highlight {:on-press on-press
                             :underlay-color "white"}
     child]))

(defn render-article
  [{:keys [url title]}]
  (clickable-wrapper #(rf/dispatch [:open-url url])
                     (article-row url title)))

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
