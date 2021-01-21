package de.htwg.se.wizard.model

import de.htwg.se.wizard.model.cardsComponent.{Card_fool, Card_with_value, Cards}
import de.htwg.se.wizard.model.gamestateComponent.GamestateBaseImpl.Gamestate
import de.htwg.se.wizard.model.gamestateComponent.GamestateInterface
import de.htwg.se.wizard.model.playerComponent.PlayerBaseImpl.Player
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class GamestateTest extends AnyWordSpec {
  "Gamestate" when {
    "initialized with these players" should {
      var game: GamestateInterface = Gamestate().set_player_amount(3)
      game = game.create_player("Max")
      game = game.create_player("Niclas")
      game = game.create_player("Prof")
      "have the players names" in {
        game.getPlayers shouldBe List(Player("Max"),Player("Niclas"),Player("Prof"))
      }
      "and when handing out the cards in round 6 they should all be different" in {
        val game0 = game.generate_Hands(5, game.getPlayers)
        (game0.getPlayers.head.getHand diff game0.getPlayers(1).getHand).size shouldBe 6
        (game0.getPlayers(1).getHand diff game0.getPlayers(2).getHand).size shouldBe 6
      }
      "and generate a trumpcard out of the remaining cards" in {
        var game1 = game
        game1 = game1.generate_Hands(5, game1.getPlayers)
        game1 = game1.set_Trump_card(game1.getPlayers, 5)
        game1.getPlayers.head.getHand.contains(game1.getTrump_card) shouldBe false
        game1.getPlayers(1).getHand.contains(game1.getTrump_card) shouldBe false
        game1.getPlayers(2).getHand.contains(game1.getTrump_card) shouldBe false

      }
      "and when the trumpcard is generated in the last round" in {
        var game8 = game
        game8 = game8.generate_Hands(19, game8.getPlayers)
        game8.set_Trump_card(game8.getPlayers, 19)
        (game8.getPlayers.head.getHand diff game8.getPlayers(1).getHand).size shouldBe 20
        (game8.getPlayers(1).getHand diff game8.getPlayers(2).getHand).size shouldBe 20
        game8.getTrump_card shouldBe Cards.all_cards(0)
      }
      "when the ticks are guessed and the round started have the guesses" in {
        var game2 = game
        game2 = game2.generate_Hands(0, game2.getPlayers)
        game2 = game2.set_guess(1)
        game2 = game2.set_guess(2)
        game2 = game2.set_guess(3)
        game2.getGame_table.last.guessed_tricks shouldBe List(1,2,3)
      }
      "when the round is complete should have the round with the results in" in {
        var game3 = game
        game3 = game3.generate_Hands(0, game3.getPlayers)
        game3 = game3.set_guess(1)
        game3 = game3.set_guess(2)
        game3 = game3.set_guess(3)
        game3 = game3.round_finished(List(1,2,2))
        game3.getGame_table.last.results shouldBe List(30,40,-10)
      }
      "when a mini round (Stich) has been played calc mini should set the idx of the winner to mini starter for next round" in {
        var game4 = game
        game4.end_mini(List(Cards.all_cards(0),Cards.all_cards(1),Cards.all_cards(58)), Cards.all_cards(20), 2).getMini_starter shouldBe 1
      }
      "when the game is over (2 Rounds in this case) and calc total is called should give the total points" in {
        var game5 = game
        game5 = game5.generate_Hands(0, game5.getPlayers)
        game5 = game5.set_guess(1)
        game5 = game5.set_guess(2)
        game5 = game5.set_guess(3)
        game5 = game5.round_finished(List(1,2,2))
        game5 = game5.generate_Hands(1, game5.getPlayers)
        game5 = game5.set_guess(2)
        game5 = game5.set_guess(3)
        game5 = game5.set_guess(1)
        game5 = game5.round_finished(List(1,2,2))
        game5.calc_total() shouldBe List(60,80,-20)
      }
      "When a player played a card should have the remaining cards" in {
        var game6 = game
        game6 = game6.generate_Hands(5, game6.getPlayers)
        val to_play = game6.getPlayers.head.getHand.head
        game6 = game6.playCard(to_play, game6.getPlayers.head)
        game6.getPlayers.head.getHand.contains(to_play) shouldBe false
      }
      "have the wished color blue " in {
        var game9 = game
        game9 = game9.wish_trumpcard("blue")
        game9.getTrump_card shouldBe new Card_with_value(1,"blue")
        game9 = game9.wish_trumpcard("green")
        game9.getTrump_card shouldBe new Card_with_value(1,"green")
        game9 = game9.wish_trumpcard("yellow")
        game9.getTrump_card shouldBe new Card_with_value(1,"yellow")
        game9 = game9.wish_trumpcard("red")
        game9.getTrump_card shouldBe new Card_with_value(1,"red")
      }
      "still have the first narr as trump after a narr has been played" in {
        var game10 = game
        game10 = game10.playCard(new Card_fool(0, "blue"), game10.getPlayers(game10.getActive_player_idx))
        game10 = game10.playCard(new Card_fool(0, "red"), game10.getPlayers(game10.getActive_player_idx))
        game10.getServe_card shouldBe new Card_fool(0, "blue")
      }
    }
  }
}