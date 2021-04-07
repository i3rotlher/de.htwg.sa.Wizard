package de.htwg.sa.wizard.util

import de.htwg.sa.wizard.model.gamestateComponent.GamestateInterface

trait PlayerStrategy {
  def strategy(amount_of_players: Int): GamestateInterface

  def strategy_3_players(): GamestateInterface

  def strategy_4_players(): GamestateInterface

  def strategy_5_players(): GamestateInterface

  def strategy_6_players(): GamestateInterface
}