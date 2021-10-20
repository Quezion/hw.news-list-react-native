(ns example.app
  (:require [example.events]
            [example.subs]
            [example.widgets :refer [button]]
            [expo.root :as expo-root]
            ["expo-status-bar" :refer [StatusBar]]
            [re-frame.core :as rf]
            ["react-native" :as rn]
            [reagent.core :as r]))

(defn root []
  [:> rn/View {:style {:flex 1
                       :padding-vertical 50
                       :justify-content :space-between
                       :align-items :center
                       :background-color :white}}
   (let [articles @(rf/subscribe [:get-articles])]
     (into [:> rn/View {:style {:align-items :center}}]
           (for [{:keys [title date url]} articles]
             [button {:on-press #(rf/dispatch [:open-url url])
                      :style {:font-weight   :bold
                              :font-size     14
                              :color         :orange
                              :margin-bottom 20
                              :border-radius 50
                              }} title])))
   [:> StatusBar {:style "auto"}]])

(defn start
  {:dev/after-load true}
  []
  (expo-root/render-root (r/as-element [root])))

(defn init []
  (rf/dispatch-sync [:initialize-db])
  (start))
