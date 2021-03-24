package de.htwg.se.wizard.model.FileIO

import de.htwg.se.wizard.model.gamestateComponent.GamestateInterface

import scala.swing.event.Event

trait File_IO_Interface {
  def load: (GamestateInterface, String)
  def save(gamestate: GamestateInterface, state: Event):Unit
}
