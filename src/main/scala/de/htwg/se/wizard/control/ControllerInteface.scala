package de.htwg.se.wizard.control

import de.htwg.se.wizard.model.FileIO.File_IO_Interface
import de.htwg.se.wizard.model.cardsComponent.Card
import de.htwg.se.wizard.model.gamestateComponent.GamestateInterface
import de.htwg.se.wizard.model.playerComponent.PlayerBaseImpl.Player

import scala.swing.Publisher
import scala.swing.event.Event

trait ControllerInteface extends Publisher{
    def player_amount(): Int
    def set_trump_card(): GamestateInterface
    def set_guess(guess: Int): GamestateInterface
    def play_card(want_to_play: Card): GamestateInterface
    def card_playable(active_player: Player, want_to_play: Card, serve_card: Card): Boolean
    def start_round(round_number: Int): GamestateInterface
    def get_player(idx: Int) : Player
    def active_player_idx(): Int
    def get_mini_winner(): Player
    def wish_trump(color: String) : GamestateInterface
    def set_player_amount(amount: Option[Int]): GamestateInterface
    def create_player(player_name: String): GamestateInterface
    def undo_player(): Unit
    def redo_player(): Unit
    def getGamestate(): GamestateInterface
    def setGamestate(gamestate: GamestateInterface): Unit
    val undoManager = new de.htwg.se.wizard.util.UndoManager()
    val file_io: File_IO_Interface
    def load(): Unit
    def save(state: Event): Unit
}
