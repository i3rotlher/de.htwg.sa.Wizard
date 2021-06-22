package de.htwg.sa.wizard.model.cardsComponent

import de.htwg.sa.wizard.model.gamestateComponent.GamestateBaseImpl.{Calc, Random}

case object Cards extends Calc with Random {

  val all_cards: Array[Card] = Array(
    new Card_fool(0, "none(green)"),
    new Card_fool(0, "none(yellow)"),
    new Card_fool(0, "none(blue)"),
    new Card_fool(0, "none(red)"),

    new Card_with_value(1, "green"),
    new Card_with_value(1, "yellow"),
    new Card_with_value(1, "blue"),
    new Card_with_value(1, "red"),

    new Card_with_value(2, "green"),
    new Card_with_value(2, "yellow"),
    new Card_with_value(2, "blue"),
    new Card_with_value(2, "red"),

    new Card_with_value(3, "green"),
    new Card_with_value(3, "yellow"),
    new Card_with_value(3, "blue"),
    new Card_with_value(3, "red"),

    new Card_with_value(4, "green"),
    new Card_with_value(4, "yellow"),
    new Card_with_value(4, "blue"),
    new Card_with_value(4, "red"),

    new Card_with_value(5, "green"),
    new Card_with_value(5, "yellow"),
    new Card_with_value(5, "blue"),
    new Card_with_value(5, "red"),

    new Card_with_value(6, "green"),
    new Card_with_value(6, "yellow"),
    new Card_with_value(6, "blue"),
    new Card_with_value(6, "red"),

    new Card_with_value(7, "green"),
    new Card_with_value(7, "yellow"),
    new Card_with_value(7, "blue"),
    new Card_with_value(7, "red"),

    new Card_with_value(8, "green"),
    new Card_with_value(8, "yellow"),
    new Card_with_value(8, "blue"),
    new Card_with_value(8, "red"),

    new Card_with_value(9, "green"),
    new Card_with_value(9, "yellow"),
    new Card_with_value(9, "blue"),
    new Card_with_value(9, "red"),

    new Card_with_value(10, "green"),
    new Card_with_value(10, "yellow"),
    new Card_with_value(10, "blue"),
    new Card_with_value(10, "red"),

    new Card_with_value(11, "green"),
    new Card_with_value(11, "yellow"),
    new Card_with_value(11, "blue"),
    new Card_with_value(11, "red"),

    new Card_with_value(12, "green"),
    new Card_with_value(12, "yellow"),
    new Card_with_value(12, "blue"),
    new Card_with_value(12, "red"),

    new Card_with_value(13, "green"),
    new Card_with_value(13, "yellow"),
    new Card_with_value(13, "blue"),
    new Card_with_value(13, "red"),

    new Card_wizard(14, "none(green)"),
    new Card_wizard(14, "none(yellow)"),
    new Card_wizard(14, "none(blue)"),
    new Card_wizard(14, "none(red)")
  )
}
