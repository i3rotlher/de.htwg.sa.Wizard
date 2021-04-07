package de.htwg.sa.wizard.FileIO
import de.htwg.sa.wizard.FileIO.XML.Impl_XML
import de.htwg.sa.wizard.control.controllerBaseImpl.{Controller, next_guess}
import de.htwg.sa.wizard.model.cardsComponent.{Card, Cards}
import de.htwg.sa.wizard.model.gamestateComponent.GamestateBaseImpl.{Gamestate, Round}
import de.htwg.sa.wizard.model.gamestateComponent.GamestateInterface
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec


class Impl_XMLTEST extends AnyWordSpec {
  "an XML" when{
    "saving an reloading the game" should{
      "have the same gamestate " in {
        val xml: de.htwg.sa.wizard.FileIO.File_IO_Interface = new Impl_XML()
        var game: GamestateInterface = Gamestate().set_player_amount(3)
        //Kartensätze
        val cards_player_1 = List(Cards.all_cards(4), Cards.all_cards(5), Cards.all_cards(6), Cards.all_cards(7))
        val cards_player_2 = List(Cards.all_cards(8), Cards.all_cards(9), Cards.all_cards(10), Cards.all_cards(11))
        val cards_player_3 = List(Cards.all_cards(12), Cards.all_cards(13), Cards.all_cards(14), Cards.all_cards(15))

        //Spieler
        val player_1 = new Player("Max", cards_player_1)
        val player_2 = new Player("Niclas", cards_player_2)
        val player_3 = new Player("Prof", cards_player_3)
        var players = List(player_1, player_2, player_3)
        //gametabel
        val round_1 = new Round(List(1, 1, 1), List(30, -10, -10))
        val round_2 = new Round(List(0, 1, 1), List(20, 30, 30)) //evtl zu änder falls Results gesammtwert speichert
        val round_3 = new Round(List(1, 1, 1), List(30, 30, 30))
        val game_table = List(round_1, round_2, round_3)

        //einzeln
        val round_number = 4
        val active_player_idx = 1
        val trump_card = Cards.all_cards(16)
        val serve_card = Cards.all_cards(17)
        val made_tricks = List(0, 0, 0)
        val played_Cards = List[Card](Cards.all_cards(0))//Karte erzeugt ungültigen Gamestate -> bei Bedarf entfernen
        val mini_starter_idx = 1
        val mini_played_counter = 0
        val state = new Gamestate(players = players, game_table = game_table,
          round_number = round_number, active_Player_idx = active_player_idx,
          trump_Card = trump_card, serve_card = serve_card, made_tricks = made_tricks, playedCards = played_Cards,
          mini_starter_idx = mini_starter_idx, mini_played_counter = mini_played_counter)


        val controller = new Controller(state, xml)
        controller.save(new next_guess)
        controller.load()
        //lesen
        state shouldBe controller.game
      }
    }
  }
}
