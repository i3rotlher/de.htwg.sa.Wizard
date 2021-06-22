package de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl

import de.htwg.sa.wizard.model.cardsComponent.Card

trait Player_Interface {

  // Variablen
  def hand: List[Card]
  def name: String

  def setHand(cards:List[Card]): Player
  def setName(name: String): Player
  def showHand(): String
  def playCard(card:Card):Player
}
