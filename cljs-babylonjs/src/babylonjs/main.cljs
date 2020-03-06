(ns babylonjs.main
  (:require [rum.core :as rum]
            [goog.dom :as dom]
            [cljsjs.babylon]))

(defn scene
  [engine canvas]
  (let [scene  (new js/BABYLON.Scene engine)
        camera (doto (new js/BABYLON.FreeCamera "camera"
                          (new js/BABYLON.Vector3 0 5 -10)
                          scene)
                 (.setTarget (js/BABYLON.Vector3.Zero))
                 (.attachControl canvas false))
        light  (new js/BABYLON.HemisphericLight "light1"
                    (new js/BABYLON.Vector3 0 1 0)
                    scene)
        sphere (doto (new js/BABYLON.MeshBuilder.CreateSphere
                          "sphere"
                          #js {:segments 16
                               :diameter 2}
                          scene)
                 (as-> sphere
                     (set! (.. sphere -position -y) 1)))
        ground (new js/BABYLON.MeshBuilder.CreateGround
                    "ground1"
                    #js {:height   6
                         :width 6
                         :subdivisions 2}
                    scene)]
    scene))

(def babylonjs
  {:did-mount    (fn [state]
                   (let [engine         (new js/BABYLON.Engine (rum/dom-node state) true)
                         scene          (scene engine (rum/dom-node state))
                         event-listener #(.resize engine)]

                     (.runRenderLoop engine #(.render scene))
                     (.addEventListener js/window "resize" event-listener)

                     (assoc state
                            ::engine engine
                            ::event-listener event-listener)))
   :will-unmount (fn [state]
                   (.removeEventListener js/window "resize" (::event-listener state))
                   (.dispose (::engine state))
                   state)})

(rum/defc main
  < babylonjs
  []
  [:canvas {:style {:width        "100%"
                    :height       "100%"
                    :touch-action "none"}}])

(rum/mount (main) (dom/getElement "app"))
