package de.htwg.se.wizard.model.FileIO.XML

import de.htwg.se.wizard.model.FileIO.File_IO_Interface
import de.htwg.se.wizard.model.cardsComponent.{Card, Card_with_value}
import de.htwg.se.wizard.model.gamestateComponent.GamestateBaseImpl.{Gamestate, Round}
import de.htwg.se.wizard.model.gamestateComponent.GamestateInterface
import de.htwg.se.wizard.model.playerComponent.PlayerBaseImpl.Player

import scala.swing.event.Event
import scala.xml._

case class Impl_XML() extends File_IO_Interface {

  override def load: (GamestateInterface, String) = {
    val file = scala.xml.XML.loadFile("gamestate.xml")

    val players = file \\ "Player"
    var player_list = List[Player]()
    for (p <- players.indices) {
      val name = (players(p)\ "name").text
      var hand = List[Card]()
      for (card <- players(p) \\ "Card") {
        hand = hand.appended(new Card_with_value((card \ "value").text.trim().replace("\n", "").toInt, (card \ "color").text.trim().replace("\n", "")))
      }
      player_list = player_list.appended(Player(name, hand))
    }

    val round_number = (file \\"Wizard" \\ "Roundnumber"\ "value").text.trim().replace("\n", "").toInt

    val rounds = file \\ "Round"
    var gametable = List[Round]()
    for (r <- rounds.indices) {
      var tricks = List[Int]()
      var result = List[Int]()
      for (res <- players.indices) {
        tricks = tricks.appended(((rounds(r) \\ "trick")(res)).text.trim().replace("\n", "").toInt)
        if (r == round_number) {
        } else {
          result = result.appended(((rounds(r) \\ "value") (res)).text.trim().replace("\n", "").toInt)
        }
      }
      gametable = gametable.appended(Round(tricks, result))
    }

    val made = file \\ "Made_tricks" \ "trick"
    var made_tricks = List[Int]()
    for (tr <- made.indices) {
      made_tricks = made_tricks.appended(made(tr).text.trim().replace("\n", "").toInt)
    }

    val played = file \\ "Played_cards" \ "Card"
    var played_cards = List[Card]()
    for (card <- played.indices) {
      played_cards = played_cards.appended(new Card_with_value((played(card) \\ "value").text.toInt, (played(card) \\ "color").text.trim().replace("\n", "")))
    }

    val state = (file \\"Wizard" \ "State").text
    val active_player_idx = (file \\"Wizard" \\ "Active_player_idx" \"value").text.trim().replace("\n", "").toInt
    val trump_card = new Card_with_value((file \\ "Trump_card" \"Card"\"value").text.trim().replace("\n", "").toInt, (file \\"Wizard" \\ "Trump_card" \"Card"\"color").text)
    val serve_card = new Card_with_value((file \\ "Serve_card" \"Card"\"value").text.trim().replace("\n", "").toInt, (file \\"Wizard" \\ "Serve_card" \"Card"\"color").text)
    print(trump_card + " ; " + serve_card)
    val Mini_starter_idx = (file \\"Wizard" \\ "Mini_starter_idx").text.trim().replace("\n", "").toInt
    val mini_played_counter = (file \\"Wizard" \\ "Mini_played_counter").text.trim().replace("\n", "").toInt
    (Gamestate(players = player_list, game_table = gametable, round_number = round_number, trump_Card = trump_card,
      serve_card = serve_card, made_tricks = made_tricks, playedCards = played_cards, mini_starter_idx = Mini_starter_idx,
      mini_played_counter = mini_played_counter, active_Player_idx = active_player_idx),state)
  }

  override def save(gamestate: GamestateInterface, state: Event): Unit = scala.xml.XML.save("gamestate.xml", generate_XML(gamestate, state))

  def generate_XML(game: GamestateInterface, state: Event): Elem = {
    <Wizard>
      <State>{state.getClass.toString.replace("class de.htwg.se.wizard.control.controllerBaseImpl.", "")}</State>
      {gamestate_toXML(game)}
    </Wizard>
  }

  def gamestate_toXML(game: GamestateInterface): Elem = {
    <Gamestate>
      {players_toXML(game.players)}
      {gametabel_toXML(game.game_table)}
      <Roundnumber>{value_toXML(game.round_number)}</Roundnumber>
      <Active_player_idx>{value_toXML(game.active_Player_idx)}</Active_player_idx>
      <Trump_card>{card_toXML(game.trump_Card)}</Trump_card>
      <Serve_card>{card_toXML(game.serve_card)}</Serve_card>
      <Made_tricks>{for (i <- game.made_tricks.indices) yield trick_toXML(game.made_tricks(i))}</Made_tricks>
      <Played_cards>{for (i <- game.playedCards.indices) yield card_toXML(game.playedCards(i))}</Played_cards>
      <Mini_starter_idx>{game.mini_starter_idx}</Mini_starter_idx>
      <Mini_played_counter>{game.mini_played_counter}</Mini_played_counter>
    </Gamestate>
  }

  def players_toXML(players: List[Player]): Elem = {
    <Players>
      {for (i <- players.indices) yield player_toXML(players(i))}
    </Players>
  }

  def player_toXML(player: Player): Elem = {
    <Player>
      <name>{player.getName}</name>
      <handcards>{cards_toXML(player.getHand)}</handcards>
    </Player>
  }

  def cards_toXML(cards: List[Card]): Elem = {
    <Cards>
      {for (i <- cards.indices) yield card_toXML(cards(i))}
    </Cards>
  }

  def card_toXML(card: Card): Elem = {
    <Card>
      <value>{card.num}</value>
      <color>{card.colour}</color>
    </Card>
  }


  def gametabel_toXML(game_table: List[Round]): Elem = {
    <Gametabel>
      {for (i <- game_table.indices) yield round_toXML(game_table(i))}
    </Gametabel>
  }

  def round_toXML(round: Round): Elem = {
    <Round>
      <Guessed_tricks>
        {for (i <- round.guessed_tricks.indices) yield trick_toXML(round.guessed_tricks(i))}
      </Guessed_tricks>
      <Result>
        {for (i <- round.results.indices) yield value_toXML(round.results(i))}
      </Result>
    </Round>
  }

  def value_toXML(number: Int):Elem = {
    <value>
      {number.toString}
    </value>
  }

  def trick_toXML(i: Int):Elem = {
    <trick>
      {i.toString}
    </trick>
  }

}
