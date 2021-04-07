package de.htwg.sa.wizard.model

import de.htwg.sa.wizard.model.cardsComponent.{Card_fool, Card_with_value, Cards}
import de.htwg.sa.wizard.model.gamestateComponent.GamestateBaseImpl.Gamestate
import de.htwg.sa.wizard.model.gamestateComponent.GamestateInterface
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import de.htwg.sa.wizard.model.cardsComponent.Card_fool
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
        game.players shouldBe List(Player("Max"),Player("Niclas"),Player("Prof"))
      }
      "and when handing out the cards in round 6 they should all be different" in {
        val game0 = game.generate_Hands(5, game.players)
        (game0.players.head.hand diff game0.players(1).hand).size shouldBe 6
        (game0.players(1).hand diff game0.players(2).hand).size shouldBe 6
      }
      "and generate a trumpcard out of the remaining cards" in {
        var game1 = game
        game1 = game1.generate_Hands(5, game1.players)
        game1 = game1.set_Trump_card(game1.players, 5)
        game1.players.head.hand.contains(game1.trump_Card) shouldBe false
        game1.players(1).hand.contains(game1.trump_Card) shouldBe false
        game1.players(2).hand.contains(game1.trump_Card) shouldBe false

      }
      "and when the trumpcard is generated in the last round" in {
        var game8 = game
        game8 = game8.generate_Hands(19, game8.players)
        game8.set_Trump_card(game8.players, 19)
        (game8.players.head.hand diff game8.players(1).hand).size shouldBe 20
        (game8.players(1).hand diff game8.players(2).hand).size shouldBe 20
        game8.trump_Card shouldBe Cards.all_cards(0)
      }
      "when the ticks are guessed and the round started have the guesses" in {
        var game2 = game
        game2 = game2.generate_Hands(0, game2.players)
        game2 = game2.set_guess(1)
        game2 = game2.set_guess(2)
        game2 = game2.set_guess(3)
        game2.game_table.last.guessed_tricks shouldBe List(1,2,3)
      }
      "when the round is complete should have the round with the results in" in {
        var game3 = game
        game3 = game3.generate_Hands(0, game3.players)
        game3 = game3.set_guess(1)
        game3 = game3.set_guess(2)
        game3 = game3.set_guess(3)
        game3 = game3.round_finished(List(1,2,2))
        game3.game_table.last.results shouldBe List(30,40,-10)
      }
      "when a mini round (Stich) has been played calc mini should set the idx of the winner to mini starter for next round" in {
        var game4 = game
        game4.end_mini(List(Cards.all_cards(0),Cards.all_cards(1),Cards.all_cards(58)), Cards.all_cards(20), 2).mini_starter_idx shouldBe 1
      }
      "when the game is over (2 Rounds in this case) and calc total is called should give the total points" in {
        var game5 = game
        game5 = game5.generate_Hands(0, game5.players)
        game5 = game5.set_guess(1)
        game5 = game5.set_guess(2)
        game5 = game5.set_guess(3)
        game5 = game5.round_finished(List(1,2,2))
        game5 = game5.generate_Hands(1, game5.players)
        game5 = game5.set_guess(2)
        game5 = game5.set_guess(3)
        game5 = game5.set_guess(1)
        game5 = game5.round_finished(List(1,2,2))
        game5.calc_total() shouldBe List(60,80,-20)
      }
      "When a player played a card should have the remaining cards" in {
        var game6 = game
        game6 = game6.generate_Hands(5, game6.players)
        val to_play = game6.players.head.hand.head
        game6 = game6.playCard(to_play, game6.players.head)
        game6.players.head.hand.contains(to_play) shouldBe false
      }
      "have the wished color blue " in {
        var game9 = game
        game9 = game9.wish_trumpcard("blue")
        game9.trump_Card shouldBe new Card_with_value(1,"blue")
        game9 = game9.wish_trumpcard("green")
        game9.trump_Card shouldBe new Card_with_value(1,"green")
        game9 = game9.wish_trumpcard("yellow")
        game9.trump_Card shouldBe new Card_with_value(1,"yellow")
        game9 = game9.wish_trumpcard("red")
        game9.trump_Card shouldBe new Card_with_value(1,"red")
      }
      "still have the first narr as trump after a narr has been played" in {
        var game10 = game
        game10 = game10.playCard(new Card_fool(0, "blue"), game10.players(game10.active_Player_idx))
        game10 = game10.playCard(new Card_fool(0, "red"), game10.players(game10.active_Player_idx))
        game10.serve_card shouldBe new Card_fool(0, "blue")
      }
    }
  }
}