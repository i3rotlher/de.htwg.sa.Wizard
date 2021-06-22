package de.htwg.sa.wizard.model

import de.htwg.sa.wizard.model.cardsComponent.{Card, Card_with_value}
import de.htwg.sa.wizard.model.cardsComponent.Card_with_value
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class CardTest extends AnyWordSpec {
  "A Card" when {
    "created" should {
      val card = new Card_with_value(1, "blue")
      "have a number" in {
        card.num should be(1)
      }
      "and have a colour" in {
        card.colour should be("blue")
      }
      "and tOString should look like" in{
        card.toString should be("(1, blue)")
      }
      "and when used unapply" in {
       Card.unapply(card) shouldBe Some(1, "blue")
      }
    }
  }
}