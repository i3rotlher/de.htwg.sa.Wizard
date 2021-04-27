package de.htwg.sa.wizard.model

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class RoundTest extends AnyWordSpec {
  "A Round" when {
    "created with the ticks guessed" should {
      var round = Round(guessed_tricks = List(0, 1, 2, 3))
        "have the values" in {
          round.guessed_tricks should be(List(0, 1, 2, 3))
        }
        "and no values in results" in{
          round.results should be(List.empty)
        }
        "and when the round is over madeTricks is called" +
        "and when done should have the results" in {
          round = Round(guessed_tricks = List(0, 1, 2, 3))
          round = round.madeTricks(List(0, 1, 3, 3))
          round.results should be(List(20, 30, -10, 50))
        }
        "and toString should look like" in {
          round = Round(guessed_tricks = List(0, 1, 2, 3))
          round = round.madeTricks(List(0, 1, 3, 3))
          round.toString should be("Tricks guessed List(0, 1, 2, 3); Results List(20, 30, -10, 50)")
       }
    }
  }
}