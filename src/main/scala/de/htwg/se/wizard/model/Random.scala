package de.htwg.se.wizard.model

trait Random {
  def generateHand(round: Int, used : List[Card] = List[Card]()) : (List[Card], List[Card]) = {
    var handCards = List[Card]().empty
    var usedCards = used
    for (_ <-0 until round){
      val nextCard = (Cards.all_cards diff usedCards)(scala.util.Random.between(0,60-usedCards.size))
      handCards = nextCard :: handCards
      usedCards = nextCard :: usedCards
    }
    (handCards,usedCards)
  }
}
