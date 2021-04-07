package de.htwg.sa.wizard.model

import de.htwg.sa.wizard.control.controllerBaseImpl.Controller
import de.htwg.sa.wizard.model.cardsComponent.{Card, Card_with_value}
import de.htwg.sa.wizard.model.gamestateComponent.GamestateBaseImpl.{Gamestate, Round}
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import de.htwg.sa.wizard.model.cardsComponent.Card_with_value
import de.htwg.sa.wizard.model.gamestateComponent.GamestateBaseImpl.Gamestate
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class StrategyTest extends AnyWordSpec {
  "A Strategy" when {
    "created in diffrent szenarios" should {
      val controller = new Controller(Gamestate(), null)
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
        val controller2 = new Controller(game, null)
        controller2.play_card(player1.hand.head)
        controller2.play_card(player2.hand.head)
        controller2.game.playedCards.contains(player2.hand.head) shouldBe false
      }

      "when the card is playable and its the last player in the mini round" in {
        val player1 = Player("eins", List[Card](new Card_with_value(1, "yellow"), new Card_with_value(3, "yellow")))
        val player2 = Player("zwei", List[Card](new Card_with_value(2, "red"), new Card_with_value(2, "yellow")))
        val player3 = Player("drei", List[Card](new Card_with_value(6, "yellow"), new Card_with_value(5, "yellow")))
        var game = Gamestate(round_number = 3, players = List(player1,player2,player3), made_tricks = List.fill(3){0})
        val controller3 = new Controller(game, null)
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
        val controller4 = new Controller(game, null)
        val old_roundNumber = controller4.game.round_number
        controller4.play_card(player1.hand.head)
        controller4.play_card(player2.hand(1))
        controller4.play_card(player3.hand.head)
        controller4.game.round_number shouldBe old_roundNumber +1

      }
      "when the card is playable and its the last player in the round and the game is over" in {

        val player1 = Player("eins", List[Card](new Card_with_value(1, "yellow"), new Card_with_value(3, "yellow")))
        val player2 = Player("zwei", List[Card](new Card_with_value(2, "red"), new Card_with_value(2, "yellow")))
        val player3 = Player("drei", List[Card](new Card_with_value(6, "yellow"), new Card_with_value(5, "yellow")))
        val game = Gamestate(mini_played_counter = 19, round_number = 19, players = List(player1,player2,player3), made_tricks = List.fill(3){0}, game_table = List(Round(List.fill(3){0})))
        val controller5 = new Controller(game, null)
        controller5.play_card(player1.hand.head)
        controller5.play_card(player2.hand(1))
        controller5.play_card(player3.hand.head)
        controller5.game.round_number shouldBe 20

      }
    }
  }
}