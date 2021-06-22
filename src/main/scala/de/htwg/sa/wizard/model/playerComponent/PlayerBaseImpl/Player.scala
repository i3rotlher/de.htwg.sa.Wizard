package de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl

import de.htwg.sa.wizard.model.cardsComponent.Card

case class Player (name: String = "unknown", hand: List[Card] = List[Card]()) extends Player_Interface {

    override def toString:String = name

    def showHand(): String = {
        var string = ""
        for (x <- 1 to hand.size) string += x + " = " + hand(x-1) + "; "
        string
    }

    def playCard(card:Card):Player = Player(name, hand diff List(card))

    override def setHand(cards: List[Card]) : Player = copy(hand = cards)

    override def setName(name: String) = Player(name)
}


class Builder() {
    var name: String = "unknown"
    var hand: List[Card] = List[Card]()

    def with_name(name: String): Builder = {
        this.name = name
        this
    }

    def with_hand(hand : List[Card]) : Builder = {
        this.hand = hand
        this
    }

    def build(): Player = {
        Player(name, hand)
    }
}

