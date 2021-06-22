package de.htwg.sa.wizard.model

import de.htwg.sa.wizard.model.cardsComponent.{Card_fool, Cards}
import de.htwg.sa.wizard.model.cardsComponent.Card_fool
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class CardsTest extends AnyWordSpec {

    "all_cards" should {
      "have a the card" in {
        Cards.all_cards(0) should be(new Card_fool(0,"none(green)"))
      }
    }
}