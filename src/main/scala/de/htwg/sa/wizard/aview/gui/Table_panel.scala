package de.htwg.sa.wizard.aview.gui

import de.htwg.sa.wizard.model.gamestateComponent.GamestateBaseImpl.GamestateInterface

import scala.swing._

//columns are moveable
class Table_panel(gamestate: GamestateInterface) extends BoxPanel(Orientation.Vertical) {
  var game_table = gamestate.game_table

  preferredSize = new Dimension(gamestate.players.size*50, (60/gamestate.players.size*18.5).toInt)


  var names = Array.fill(gamestate.players.size){""}
  for ((player,x) <- gamestate.players.zipWithIndex) {
    names(x) = player.name
  }

  var rounds: Array[Array[Any]] = Array.fill(60/gamestate.players.size,gamestate.players.size){"        |  "}

  if (game_table.size -1 > 0) {
    for ((round, x) <- game_table.zipWithIndex) {
      if (x != game_table.size-1) {
        for (y <- gamestate.players.indices) {
          rounds(x)(y) = "  " + round.results(y) + "   | " + round.guessed_tricks(y)
        }
      }
    }
  }
  val table: Table = new Table(rounds ,names.toSeq) {
    enabled = false
  }

  val table_scroll_pane = new ScrollPane(table) {
    contents = table
  }
  contents+=table_scroll_pane
}
