package de.htwg.se.wizard.model.playerComponent.PlayerBaseImpl

import de.htwg.se.wizard.model.cardsComponent.Card

case class Player (name: String = "unknown", hand: List[Card] = List[Card]()) {

    override def toString:String = name

    def showHand(): String = {
        var string = ""
        for (x <- 1 to hand.size) string += x + " = " + hand(x-1) + "; "
        string
    }

    def playCard(card:Card):Player = Player(name, hand diff List(card))

    def getHand: List[Card] = hand

    def getName: String = name

    def setName(name:String) :Player = Player(name, hand)

    def setHand(cards: List[Card]) :Player = Player(name, cards)
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

