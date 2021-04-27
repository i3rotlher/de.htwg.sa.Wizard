package de.htwg.sa.wizard.model.GamestateComponent.GamestateBaseImpl

trait Calc {

  def isPlayable(serve_card: Card, toPlay: Card, handCards: Iterable[Card]): Boolean = {
    if (toPlay.num == 0 || toPlay.num == 14)
      return true
    if (serve_card.colour == toPlay.colour)
      return true
    if (serve_card.colour.contains("none"))
      return true
    for (comp_Card <- handCards.filterNot(_ == toPlay)) {
      if (comp_Card.colour == serve_card.colour)
        return false
    }
    true
  }

  def calcWinner(playedCards: Iterable[Card], trumpColour: String = "none"): Int = {
    var winner: Int = 0
    var winning_Card = playedCards.head
    if (winning_Card.num == 14) {
      return winner
    }
    var counter: Int = 0
    for (comp_Card <- playedCards.tail) {
      counter += 1
      if (comp_Card.num == 14) {
        return counter
      } else if (comp_Card.colour == winning_Card.colour) {
        if (comp_Card.num > winning_Card.num) {
          winner = counter
          winning_Card = comp_Card
        }
      } else if (comp_Card.colour == trumpColour) {
        winner = counter
        winning_Card = comp_Card
      }
    }
    winner
  }
}
