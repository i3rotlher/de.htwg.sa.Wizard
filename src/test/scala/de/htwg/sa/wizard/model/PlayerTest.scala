package de.htwg.sa.wizard.model

import de.htwg.sa.wizard.model.cardsComponent.Cards
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class PlayerTest extends AnyWordSpec {
  "A player" when {
      "created" should {
        var player = Player("Karl", List(Cards.all_cards(0),Cards.all_cards(1)))
        "have a name" in {
          player.name shouldBe "Karl"
        }
        "and have the cards" in {
          player.hand shouldBe List(Cards.all_cards(0),Cards.all_cards(1))
        }
        "and when played a card" in {
          player = player.playCard(Cards.all_cards(1))
          player.hand shouldBe List(Cards.all_cards(0))
        }
        "and when the Hand is shown to the user should look like this" in{
          player.showHand() shouldBe "1 = (Narr, none(green)); "
        }
        "and toString should look like" in {
          player.toString shouldBe "Karl"
        }
        "when unapplied" in {
          Player.unapply(player).get shouldBe ("Karl", List(Cards.all_cards(0)))
        }

    }
  }
}
