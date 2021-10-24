(ns example.react
  (:require
   ["react-native" :as rn]
   [reagent.core :as r]))

(def react-native rn)

(defn get-react-property
  "Little shim to fetch these at runtime"
  [name]
  (if rn
    (aget rn name)
    #js {}))

(def view (get-react-property "View"))
(def scroll-view (get-react-property "ScrollView"))
(def text (get-react-property "Text"))
(def image (get-react-property "Image"))
(def touchable-highlight (get-react-property "TouchableHighlight"))

(def linking (get-react-property "Linking"))

;; For some views such as flat-list, the CLJS FN that renders each item requires
;; translation to the JS react layer. That's where this FN comes in
(defn wrap-render-fn
  "Wraps a Clojure render function that should take one arg and return a renderable component.
   The wrapping translates to the Javascript objects necessary for React.
   Usefor ful views such as flat-list, which require native React objects unless wrapped"
  [f]
  (fn [data]
    (let [{:keys [item index separators]} (js->clj data :keywordize-keys true)]
      (r/as-element (f (js->clj item :keywordize-keys true) index separators)))))

(def flat-list-class (get-react-property "FlatList"))

(defn keyname
  "If a key contains a namespace, returns it as string namespace/key.
   Useful for clj->js encoding, but note that when using namespaced keywords as values,
   they will not automatically be rehydrated into keywords in js->clj"
  [key]
  (or (some-> (namespace key) (str "/" (name key)))
      (name key)))

(defn flat-list
  "A simple wrapper for FlatList. Mimics the native component key-names,
   like :data and :render-item
   See https://facebook.github.io/react-native/docs/flatlist.html"
  [{:keys [data render-item] :as props}]
  [:> flat-list-class (merge props
                             {:render-item  (wrap-render-fn render-item)
                              ;; NOTE: plain clj->js is failing because namespace keys are stripped
                              ;; stale solution in https://dev.clojure.org/jira/browse/CLJS-536
                              ;; David Nolan fixed in https://dev.clojure.org/jira/browse/CLJS-2215
                              ;; UPDATE: There's no reasonable way to use namespace keywords
                              ;;         as values. The Nolan fix allows map keys to work
                              :data (clj->js data :keyword-fn keyname)

                              ;; NOTE: this keyExtractor is necessary to provide unique keys on items
                              ;;       the second argument to the FN is the unique index in the list
                              :keyExtractor (fn [item index] (str index))})])
