(ns game.core.checkpoint
  (:require
    [game.core.agendas :refer [update-all-advancement-requirements update-all-agenda-points]]
    [game.core.board :refer [get-remotes]]
    [game.core.ice :refer [update-all-ice update-all-icebreakers]]
    [game.core.hand-size :refer [update-hand-size]]
    [game.core.initializing :refer [update-all-card-labels]]
    [game.core.link :refer [update-link]]
    [game.core.memory :refer [update-mu]]
    [game.core.tags :refer [update-tag-status]]
    [game.utils :refer [dissoc-in]]))

(defn- clear-empty-remotes
  [state]
  (doseq [remote (get-remotes state)]
    (let [zone [:corp :servers (first remote)]]
      (when (and (empty? (get-in @state (conj zone :content)))
                 (empty? (get-in @state (conj zone :ices))))
        (swap! state dissoc-in zone)))))

(defn fake-checkpoint
  [state]
  (loop [i 0]
    (let [changed [(update-all-ice state :corp)
                   (update-all-icebreakers state :runner)
                   (update-all-card-labels state)
                   (update-all-advancement-requirements state)
                   (update-all-agenda-points state)
                   (update-link state)
                   (update-mu state)
                   (update-hand-size state :corp)
                   (update-hand-size state :runner)
                   (update-tag-status state)]]
      (when (and (some true? changed)
                 (< i 10))
        (recur (inc i)))))
  (clear-empty-remotes state))
