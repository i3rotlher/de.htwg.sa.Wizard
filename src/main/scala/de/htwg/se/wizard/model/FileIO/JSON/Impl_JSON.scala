package de.htwg.se.wizard.model.FileIO.JSON

import com.google.inject.Guice
import de.htwg.se.wizard.WizardModule
import de.htwg.se.wizard.model.FileIO.File_IO_Interface
import de.htwg.se.wizard.model.cardsComponent.{Card, Card_with_value}
import de.htwg.se.wizard.model.gamestateComponent.GamestateBaseImpl.{Gamestate, Round}
import de.htwg.se.wizard.model.gamestateComponent.GamestateInterface
import de.htwg.se.wizard.model.playerComponent.PlayerBaseImpl.Player
import play.api.libs.json._

import scala.io.Source
import scala.swing.event.Event

case class Impl_JSON() extends File_IO_Interface {

  override def load : (GamestateInterface, String) = {

    val injector = Guice.createInjector(new WizardModule)
    val source: String = Source.fromFile("gamestate.json").getLines.mkString
    val json = Json.parse(source)
    val round_number = (json \ "round_number").get.toString.toInt
    var serve_card = new Card_with_value((json("serve_card") \ "value").get.toString().toInt, (json("serve_card") \ "color").get.toString().replace("\"", ""))
    val player_amount = (json \ "player_amount").get.toString.toInt
    val state = (json \ "state").get.toString().replace("\"", "")
    val trump_card = new Card_with_value((json("trump_card") \ "value").get.toString().toInt, (json("trump_card") \ "color").get.toString().replace("\"", ""))
    val Mini_starter_idx = (json \ "mini_starter_idx").get.toString.toInt
    val mini_played_counter = (json \ "mini_played_counter").get.toString.toInt
    val active_player_idx = (json \ "active_player_idx").get.toString().toInt


    val players_names = Json.parse((json \ "players_names").get.toString()).as[List[JsValue]]
    val players_hands = Json.parse((json \ "players_hands").get.toString()).as[List[JsValue]]
    var player_list = List[Player]()
    for (p <- players_names.indices) {
      val name = (players_names(p)).toString().replace("\"", "")
      var hand = List[Card]()
      val player_hand = Json.parse(players_hands(p).toString()).as[List[JsValue]]
      for (card <- 0 until player_hand.size) {
        hand = hand.appended(new Card_with_value((player_hand(card) \ "value").get.toString().toInt, (player_hand(card) \ "color").get.toString().replace("\"", "")))
      }
      player_list = player_list.appended(Player(name, hand))
    }
    val gametable = Json.parse((json \ "game_table").get.toString()).as[List[JsValue]]
    var game_table = List[Round]()
    for (r <- gametable.indices) {
      val round = Json.parse(gametable(r).toString())
      val results = Json.parse((round \ "results").get.toString()).as[List[JsValue]]
      val guesses = Json.parse((round \ "guessed_tricks").get.toString()).as[List[JsValue]]
      var result = List[Int]()
      var guesses_made = List[Int]()
      for (y <- 0 until player_amount) {
        if (results.size != 0) {
          result = result.appended(results(y).toString().toInt)
        }
        guesses_made = guesses_made.appended(guesses(y).toString().toInt)
      }
      game_table = game_table.appended(Round(guesses_made, result))
    }

    // da nur bei guess gespeichert wird koennen nur die folgenden werte sein
    val made_tricks = List.fill(player_amount)(0)
    val played_cards = List[Card]()

    (Gamestate(players = player_list, game_table = game_table, trump_Card = trump_card,
      serve_card = serve_card, made_tricks = made_tricks, playedCards = played_cards, mini_starter_idx = Mini_starter_idx,
      mini_played_counter = mini_played_counter, active_Player_idx = active_player_idx, round_number = round_number),state)
  }

  implicit val readPlayerList = new Reads[List[Player]] {
    override def reads(json: JsValue) = {
      null
    }
  }

  override def save(gamestate: GamestateInterface, state: Event): Unit = {
    import java.io._
    val print_writer = new PrintWriter(new File("gamestate.json"))
    print_writer.write(Json.prettyPrint(gameStateToJson(gamestate, state)))
    print_writer.close()
  }

  implicit val cardWrites = new Writes[Card] {
    def writes(card: Card) = Json.obj(
      "value" -> card.num,
      "color" -> card.colour
    )
  }

  implicit val playerWrites = new Writes[Player] {
    def writes(player: Player) = { Json.obj(
      "name" -> player.name,
      "hand" -> Json.toJson(
        for (card <- player.hand) yield Json.toJson(card)
      )
    )
    }
  }

  implicit val roundWrites = new Writes[Round] {
    def writes(round: Round) = {
      Json.obj(
        "guessed_tricks" -> Json.toJson(round.guessed_tricks),
        "results" -> Json.toJson(round.results)
      )
    }
  }

  def gameStateToJson(game: GamestateInterface, state: Event) = {
    Json.obj(
      "state" -> state.getClass.toString.replace("class de.htwg.se.wizard.control.controllerBaseImpl.", ""),
      "game_table" -> game.game_table,
      "player_amount" -> game.players.size,
      "players_names" -> Json.toJson(
        for (player <- game.players) yield {
          Json.toJson(player.name)
        }
      ),
      "players_hands" -> Json.toJson(
        for (player <- game.players) yield {
          Json.toJson(player.hand)
        }
      ),
      "round_number" -> game.round_number,
      "active_player_idx" -> game.active_Player_idx,
      "trump_card" -> game.trump_Card,
      "serve_card" -> game.serve_card,
      "made_tricks" -> game.made_tricks,
      "playedCards" -> game.playedCards,
      "mini_starter_idx" -> game.mini_starter_idx,
      "mini_played_counter" -> game.mini_played_counter
    )
  }

}
