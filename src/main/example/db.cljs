(ns example.db)

(def sample-articles
  [{:title "On a Pacific Island, Russia Tests Its Battle Plan for Climate Change"
    :url "https://www.nytimes.com/2021/10/19/world/europe/russia-climate-change.html"
    :date "October 19th, 2021"}
   {:title "What Does Horror Taste Like? ‘Carnage Asada’ and Bloody Cocktails"
    :url "https://www.nytimes.com/2021/10/18/dining/new-horror-restaurants.html"
    :date "October 18th, 2021"}
   {:title "Bellinger’s Blast Breathes Life Into Dodgers Offense"
    :url "https://www.nytimes.com/2021/10/19/sports/baseball/los-angeles-atlanta-nlcs-game3-bellinger.html"
    :date "October 19th, 2021"}
   {:title "Despite a Punishing Drought, San Diego Has Water. It Wasn’t Easy."
    :url "https://www.nytimes.com/2021/10/17/us/san-diego-drought.html"
    :date "October 17th, 2021"}])

;; initial state of app-db
(defonce app-db {:articles sample-articles})
