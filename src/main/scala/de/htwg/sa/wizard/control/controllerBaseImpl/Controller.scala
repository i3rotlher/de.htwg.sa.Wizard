package de.htwg.sa.wizard.control.controllerBaseImpl

import com.google.inject.Inject
import de.htwg.sa.wizard.control.ControllerInteface
import de.htwg.sa.wizard.FileIO.File_IO_Interface
import de.htwg.sa.wizard.model.cardsComponent.{Card, Cards}
import de.htwg.sa.wizard.model.gamestateComponent.GamestateInterface
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import de.htwg.sa.wizard.model.cardsComponent.Card

import scala.swing.Publisher
import scala.swing.event.Event

case class Controller @Inject() (var game: GamestateInterface, file_io: File_IO_Interface) extends ControllerInteface with Publisher {

  def setGamestate(gamestate: GamestateInterface): Unit = game = gamestate

  def player_amount(): Int = game.players.size

  def set_trump_card(): GamestateInterface = {
    game = game.set_Trump_card(game.players, game.round_number)
    if (game.trump_Card.num == 14) {
      game = game.set_active_player_idx((active_player_idx()-1+player_amount())%player_amount())
      publish(new Wizard_trump)
      publish(new set_Wizard_trump)
      return game
    }
    publish(new next_guess)
    game
  }

  def set_guess(guess: Int): GamestateInterface = {
    game = game.set_guess(guess)
    if (game.active_Player_idx == game.round_number%player_amount()) {
      publish(new next_player_card)
      game
    } else {
      publish(new next_guess)
      game
    }
  }

  def play_card(want_to_play: Card): GamestateInterface = {
    val active_player_idx = game.active_Player_idx
    val mini_starter_idx = game.mini_starter_idx
    if (!card_playable(game.players(game.active_Player_idx), want_to_play, game.serve_card)) {
      publish(new card_not_playable)
      game
    } else {
      if (active_player_idx == (mini_starter_idx+player_amount()-1)%player_amount()) {
        game = game.playCard(want_to_play, game.players(active_player_idx))
        game = game.end_mini(game.playedCards, game.trump_Card, game.mini_starter_idx)
        publish(new mini_over)
        if (game.mini_played_counter == game.round_number + 1) {
          if (game.round_number + 1 == 60/player_amount()) {
            game = game.round_finished(game.made_tricks)
            publish(new round_over)
            publish(new game_over)
            game
          } else {
            game = game.round_finished(game.made_tricks)
            publish(new round_over)
            publish(new start_round)
            start_round(game.round_number)
            game
          }
        } else {
          publish(new next_player_card)
          game
        }
      } else {
        game = game.playCard(want_to_play, game.players(active_player_idx))
        publish(new next_player_card)
        game
      }
    }
  }

  def card_playable(active_player: Player, want_to_play: Card, serve_card: Card): Boolean = {
    Cards.isPlayable(serve_card, want_to_play, active_player.hand)
  }

  def start_round(round_number: Int): GamestateInterface = {
    game = game.generate_Hands(round_number, game.players)
    game = set_trump_card()
    game
  }

  def get_player(idx: Int) : Player = game.players(idx)

  def active_player_idx(): Int = game.active_Player_idx

  def get_mini_winner(): Player = game.players(game.mini_starter_idx)

  def wish_trump(color: String) : GamestateInterface = {
    game = game.wish_trumpcard(color)
    publish(new next_guess)
    game
  }

  def set_player_amount(amount: Option[Int]): GamestateInterface = {
    amount match {
      case Some(value) =>
        game = game.set_player_amount(value)
        publish(new player_create)
        game
      case None =>
        game
    }
  }

  def create_player(player_name: String): GamestateInterface = {
    val Undo_Player_Name = new UndoPlayerNameCommand(player_name, this)
    undoManager.doStep(Undo_Player_Name)

    if (game.active_Player_idx == 0) {
      publish(new start_round)
      start_round(game.round_number)
    } else {
      publish(new player_create)
    }
    game

  }

  def undo_player(): Unit = {
    undoManager.undoStep()
    publish(new player_create)
  }

  def redo_player(): Unit = undoManager.redoStep()

  override def getGamestate(): GamestateInterface = game

  override def load(): Unit = {
    val game_state = file_io.load
    game = game_state._1

    game_state._2 match {
      case "name_ok" => publish(new name_ok)
      case "set_Wizard_trump" => publish(new set_Wizard_trump)
      case "game_started" => publish(new game_started)
      case "get_Amount" => publish(new get_Amount)
      case "player_create" => publish(new player_create)
      case "round_started" => publish(new round_started)
      case "start_round" => publish(new start_round)
      case "Wizard_trump" => publish(new Wizard_trump)
      case "next_guess" => publish(new next_guess)
      case "next_player_card" => publish(new next_player_card)
      case "round_over" => publish(new round_over)
      case "card_not_playable" => publish(new card_not_playable)
      case "guesses_set" => publish(new guesses_set)
      case "mini_over" => publish(new mini_over)
      case "game_over" => publish(new game_over)
      case _ => println(game_state._2)
    }
  }

  override def save(state: Event): Unit = file_io.save(game, state)
}

