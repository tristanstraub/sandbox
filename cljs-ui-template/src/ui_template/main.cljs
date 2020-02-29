(ns ui-template.main
  (:require-macros [ui-template.style :as style])
  (:require [rum.core :as rum]
            [goog.dom :as dom]))

(rum/defc main
  []
  [:span
   [:style (style/css)]
   [:div.container "main"]])

(rum/mount (main) (dom/getElement "app"))
