package de.htwg.sa.wizard.model

import de.htwg.sa.wizard.model.cardsComponent.Cards
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Builder
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class BuilderTest extends AnyWordSpec {
  "A Builder" when {
    val builder = new Builder()
    "created" should {
      "have a name" in {
        builder.name shouldBe "unknown"
      }
      "and have no hand" in {
        builder.hand.isEmpty shouldBe true
      }
    }
    "after beeing invoked with the with_name" should {
        val builder1 = new Builder().with_name("Test")
        "have the name" in {
          builder1.name shouldBe "Test"
        }
    }
    "after invoked with the with_hand" should {
      val builder2 = new Builder().with_name("Test")
        builder2.with_hand(List(Cards.all_cards(0)))
        "have the hand" in {
          builder2.hand shouldBe List(Cards.all_cards(0))
        }
    }
    "beeing build()" should {
      val builder3 = new Builder().with_name("Test")
        builder3.with_hand(List(Cards.all_cards(1))).with_name("Test 2")
        "produce a player with the hand and name" in {
          builder3.build().name shouldBe "Test 2"
          builder3.build().hand shouldBe List(Cards.all_cards(1))
        }
    }
  }
}