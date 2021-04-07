package de.htwg.sa.wizard.aview.gui

import java.awt.Dimension

import de.htwg.sa.wizard.model.cardsComponent.Card

import scala.swing._

class Hand_panel(handcards: List[Card]) extends BoxPanel(Orientation.Vertical) {

  preferredSize = new Dimension(1200, 325)

  val label = new Label {
    text = "Your Hand:"
    font = new Font("Verdana", 1, 36)
  }

  val hand = new BoxPanel(Orientation.Horizontal) {
    for (card <- handcards) {
      contents+=Card_panel(card)
    }
  }

  val scrollpane = new ScrollPane(hand) {
    contents = hand
  }
  contents+=scrollpane
}