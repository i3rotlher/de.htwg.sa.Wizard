package de.htwg.sa.wizard.control.controllerBaseImpl

import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import de.htwg.sa.wizard.util.Command

class UndoPlayerNameCommand(player_name: String, controller: Controller) extends Command {

  override def doStep(): Unit = controller.game = controller.game.create_player(player_name)

  override def undoStep(): Unit = controller.game = controller.game.reset_player()

  override def redoStep(): Unit = controller.create_player(player_name)
}