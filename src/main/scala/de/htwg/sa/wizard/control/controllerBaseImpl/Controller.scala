package de.htwg.sa.wizard.control.controllerBaseImpl
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.google.inject.{Guice, Inject}
import de.htwg.sa.wizard.WizardModule
import de.htwg.sa.wizard.control.ControllerInteface
import de.htwg.sa.wizard.model.cardsComponent.{Card, Card_with_value, Cards}
import de.htwg.sa.wizard.model.gamestateComponent.GamestateBaseImpl.{Gamestate, GamestateInterface, Round}
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import play.api.libs.json.{JsValue, Json, Writes}

import scala.concurrent.Future
import scala.swing.Publisher
import scala.swing.event.Event
import scala.util.{Failure, Success}

case class Controller @Inject() (var game: GamestateInterface) extends ControllerInteface with Publisher {

  def setGamestate(gamestate: GamestateInterface): Unit = game = gamestate

  def player_amount(): Int = game.players.size

  def set_trump_card(): GamestateInterface = {
    game = game.set_Trump_card(game.players, game.round_number)
    if (game.trump_Card.num == 14) {
      game = game.set_active_player_idx((active_player_idx()-1+player_amount())%player_amount())
      publish(new Wizard_trump)
      publish(new set_Wizard_trump)
      return game
    }
    publish(new next_guess)
    game
  }

  def set_guess(guess: Int): GamestateInterface = {
    game = game.set_guess(guess)
    if (game.active_Player_idx == game.round_number%player_amount()) {
      publish(new next_player_card)
      game
    } else {
      publish(new next_guess)
      game
    }
  }

  def play_card(want_to_play: Card): GamestateInterface = {
    val active_player_idx = game.active_Player_idx
    val mini_starter_idx = game.mini_starter_idx
    if (!card_playable(game.players(game.active_Player_idx), want_to_play, game.serve_card)) {
      publish(new card_not_playable)
      game
    } else {
      if (active_player_idx == (mini_starter_idx+player_amount()-1)%player_amount()) {
        game = game.playCard(want_to_play, game.players(active_player_idx))
        game = game.end_mini(game.playedCards, game.trump_Card, game.mini_starter_idx)
        publish(new mini_over)
        if (game.mini_played_counter == game.round_number + 1) {
          if (game.round_number + 1 == 60/player_amount()) {
            game = game.round_finished(game.made_tricks)
            publish(new round_over)
            publish(new game_over)
            game
          } else {
            game = game.round_finished(game.made_tricks)
            publish(new round_over)
            publish(new start_round)
            start_round(game.round_number)
            game
          }
        } else {
          publish(new next_player_card)
          game
        }
      } else {
        game = game.playCard(want_to_play, game.players(active_player_idx))
        publish(new next_player_card)
        game
      }
    }
  }

  def card_playable(active_player: Player, want_to_play: Card, serve_card: Card): Boolean = {
    Cards.isPlayable(serve_card, want_to_play, active_player.hand)
  }

  def start_round(round_number: Int): GamestateInterface = {
    game = game.generate_Hands(round_number, game.players)
    game = set_trump_card()
    game
  }

  def get_player(idx: Int) : Player = game.players(idx)

  def active_player_idx(): Int = game.active_Player_idx

  def get_mini_winner(): Player = game.players(game.mini_starter_idx)

  def wish_trump(color: String) : GamestateInterface = {
    game = game.wish_trumpcard(color)
    publish(new next_guess)
    game
  }

  def set_player_amount(amount: Option[Int]): GamestateInterface = {
    amount match {
      case Some(value) =>
        game = game.set_player_amount(value)
        publish(new player_create)
        game
      case None =>
        game
    }
  }

  def create_player(player_name: String): GamestateInterface = {
    val Undo_Player_Name = new UndoPlayerNameCommand(player_name, this)
    undoManager.doStep(Undo_Player_Name)

    if (game.active_Player_idx == 0) {
      publish(new start_round)
      start_round(game.round_number)
    } else {
      publish(new player_create)
    }
    game

  }

  def undo_player(): Unit = {
    undoManager.undoStep()
    publish(new player_create)
  }

  def redo_player(): Unit = undoManager.redoStep()

  override def getGamestate(): GamestateInterface = game

  override def load(): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://localhost:8085/JSON"))

    responseFuture
      .onComplete {
        case Failure(_) => sys.error("something wrong")
        case Success(res) => {
          Unmarshal(res.entity).to[String].onComplete {
            case Failure(_) => sys.error("Oh FUCk unmarshell sucks")
            case Success(result) => {
              val game_state = load_json(result)
              game = game_state._1

              game_state._2 match {
                case "name_ok" => publish(new name_ok)
                case "set_Wizard_trump" => publish(new set_Wizard_trump)
                case "game_started" => publish(new game_started)
                case "get_Amount" => publish(new get_Amount)
                case "player_create" => publish(new player_create)
                case "round_started" => publish(new round_started)
                case "start_round" => publish(new start_round)
                case "Wizard_trump" => publish(new Wizard_trump)
                case "next_guess" => publish(new next_guess)
                case "next_player_card" => publish(new next_player_card)
                case "round_over" => publish(new round_over)
                case "card_not_playable" => publish(new card_not_playable)
                case "guesses_set" => publish(new guesses_set)
                case "mini_over" => publish(new mini_over)
                case "game_over" => publish(new game_over)
                case _ => println(game_state._2)
              }
            }
          }
        }
      }
  }

  def load_json(raw_gamestate: String) : (GamestateInterface, String) = {

    val injector = Guice.createInjector(new WizardModule)
    val source: String = raw_gamestate
    val json = Json.parse(source)
    val round_number = (json \ "round_number").get.toString.toInt
    var serve_card = new Card_with_value((json("serve_card") \ "value").get.toString().toInt, (json("serve_card") \ "color").get.toString().replace("\"", ""))
    val player_amount = (json \ "player_amount").get.toString.toInt
    val state = (json \ "state").get.toString().replace("\"", "").replace("class de.htwg.sa.wizard.control.controllerBaseImpl.", "")
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

  def save(state: Event): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val gamestate_json_string = Json.prettyPrint(gameStateToJson(game, state))

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(method = HttpMethods.POST ,uri = "http://localhost:8080/JSON", entity = gamestate_json_string))
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

