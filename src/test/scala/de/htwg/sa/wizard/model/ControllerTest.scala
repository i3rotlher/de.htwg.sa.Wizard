package de.htwg.sa.wizard.model

import de.htwg.sa.wizard.control.controllerBaseImpl.Controller
import de.htwg.sa.wizard.model.cardsComponent.{Card, Card_with_value}
import de.htwg.sa.wizard.model.gamestateComponent.GamestateBaseImpl.{Gamestate, Round}
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class ControllerTest extends AnyWordSpec {
  "A controller" when {

    "beeing initialized" should {
      val controller = Controller(Gamestate())
      "when called set player amount should have the player list with the given size" in {
        val controller1 = controller
        controller1.set_player_amount(Some(3))
        controller1.game.players.size shouldBe 3
        val controller2 = controller
        controller2.set_player_amount(Some(4))
        controller2.game.players.size shouldBe 4
        val controller3 = controller
        controller3.set_player_amount(Some(5))
        controller3.game.players.size shouldBe 5
        val controller4 = controller
        controller3.set_player_amount(Some(6))
        controller3.game.players.size shouldBe 6

      }
      "when invoked with a None to player_amount have choose the Option none" in {
        val controller1 = new Controller(Gamestate())
        controller1.set_player_amount(None)
        controller1.player_amount() shouldBe 0
      }
      "when given a player name should set the getPlayers name" in {
        val controller2 = new Controller(controller.game)
        controller2.set_player_amount(Some(3))
        controller2.create_player("Torsten")
        controller2.game.players(0).name shouldBe "Torsten"
      }
      "when given a player and undo is invoked should reset the name" in {
        val controller2 = new Controller(controller.game)
        controller2.set_player_amount(Some(3))
        controller2.create_player("Torsten")
        controller2.undo_player
        controller2.game.players(controller2.game.active_Player_idx).name shouldBe "unknown"
      }
      "when given a player, and undo has been alled and redo is beeing called should have the name again" in {
        val controller2 = new Controller(controller.game)
        controller2.set_player_amount(Some(3))
        controller2.create_player("Torsten")
        controller2.undo_player
        controller2.redo_player
        controller2.game.players(controller2.game.active_Player_idx-1).name shouldBe "Torsten"
      }
      "when given a wished color should set the trumpcard" in {
        val controller3 = new Controller(controller.game)
        controller3.wish_trump("red")
        controller3.game.trump_Card shouldBe new Card_with_value(1, "red")
      }
      "when get mini winner is called should return the winner idx" in {
        val controller4 = new Controller(controller.game)
        controller4.game.players.indexOf(controller4.get_mini_winner()) shouldBe 0
      }
    }

    "already beeing initialized with players" should {
      val controller = new Controller(Gamestate())
      controller.set_player_amount(Some(3))
      controller.create_player("eins")
      controller.create_player("zwei")
      controller.create_player("drei")
      "after start_round has been called" in {
        val controller1 = new Controller(controller.game)
        controller1.start_round(1)
        controller1.game.round_number shouldBe 0
      }
      "after set_guess has been called" in {
        val controller2_1 = new Controller(controller.game)
        controller2_1.start_round(0)
        controller2_1.set_guess(2)
        controller2_1.game.game_table(0).guessed_tricks(0) shouldBe(2)
      }
      "after set_guess has been called by the last player" in {
        val controller2 = new Controller(controller.game)
        controller2.start_round(0)
        controller2.set_guess(1)
        controller2.set_guess(2)
        controller2.set_guess(3).active_Player_idx shouldBe 0
      }
    }
    "created in diffrent szenarios" should {
      val controller = new Controller(Gamestate())
      controller.set_player_amount(Some(3))
      controller.create_player("eins")
      controller.create_player("zwei")
      controller.create_player("drei")
      controller.start_round(10)

      "when a player tries play a unplayable card it shouldnt be played" in {
        val player1 = Player("eins", List[Card](new Card_with_value(1, "yellow"), new Card_with_value(3, "yellow")))
        val player2 = Player("zwei", List[Card](new Card_with_value(2, "red"), new Card_with_value(2, "yellow")))
        val player3 = Player("drei", List[Card](new Card_with_value(6, "yellow"), new Card_with_value(5, "yellow")))
        val game = Gamestate(players = List(player1,player2,player3))
        val controller2 = new Controller(game)
        controller2.play_card(player1.hand.head)
        controller2.play_card(player2.hand.head)
        controller2.game.playedCards.contains(player2.hand.head) shouldBe false
      }

      "when the card is playable and its the last player in the mini round" in {
        val player1 = Player("eins", List[Card](new Card_with_value(1, "yellow"), new Card_with_value(3, "yellow")))
        val player2 = Player("zwei", List[Card](new Card_with_value(2, "red"), new Card_with_value(2, "yellow")))
        val player3 = Player("drei", List[Card](new Card_with_value(6, "yellow"), new Card_with_value(5, "yellow")))
        var game = Gamestate(round_number = 3, players = List(player1,player2,player3), made_tricks = List.fill(3){0})
        val controller3 = new Controller(game)
        val old_roundNumber = controller3.game.round_number
        controller3.play_card(player1.hand.head)
        controller3.play_card(player2.hand(1))
        controller3.play_card(player3.hand.head)
        controller3.game.round_number shouldBe old_roundNumber
      }

      "when the card is playable and its the last player in the round" in {
        val player1 = Player("eins", List[Card](new Card_with_value(1, "yellow"), new Card_with_value(3, "yellow")))
        val player2 = Player("zwei", List[Card](new Card_with_value(2, "red"), new Card_with_value(2, "yellow")))
        val player3 = Player("drei", List[Card](new Card_with_value(6, "yellow"), new Card_with_value(5, "yellow")))
        val game = Gamestate(mini_played_counter = 0, round_number = 0, players = List(player1,player2,player3), made_tricks = List.fill(3){0}, game_table = List(Round(List.fill(3){0})))
        val controller4 = new Controller(game)
        val old_roundNumber = controller4.game.round_number
        controller4.play_card(player1.hand.head)
        controller4.play_card(player2.hand(1))
        controller4.play_card(player3.hand.head)
        controller4.game.round_number shouldBe old_roundNumber +1

      }
      //      "when the card is playable and its the last player in the round and the game is over" in {
      //        val observer = new Observer {
      //          var updated: State.Value = State.game_started
      //
      //          def isUpdated: State.Value = updated
      //
      //          override def update(status: State.Value): Boolean = {
      //            updated = status; println(status); true
      //          }
      //        }
      //        val player1 = Player("eins", List[Card](new Card_with_value(1, "yellow"), new Card_with_value(3, "yellow")))
      //        val player2 = Player("zwei", List[Card](new Card_with_value(2, "red"), new Card_with_value(2, "yellow")))
      //        val player3 = Player("drei", List[Card](new Card_with_value(6, "yellow"), new Card_with_value(5, "yellow")))
      //        val game = Gamestate(mini_played_counter = 19, round_number = 19, players = List(player1,player2,player3), made_tricks = List.fill(3){0}, game_table = List(Round(List.fill(3){0})))
      //        val controller5 = new Controller(game)
      //        controller5.add(observer)
      //        controller5.play_card(player1.hand.head)
      //        controller5.play_card(player2.hand(1))
      //        controller5.play_card(player3.hand.head)
      //        observer.updated shouldBe State.game_over
      //
      //      }
    }
  }
}
