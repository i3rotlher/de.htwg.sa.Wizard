package de.htwg.sa.wizard.model.GamestateComponent.GamestateBaseImpl

import de.htwg.sa.wizard.model.cardsComponent.{Card, Card_with_value, Cards}
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import de.htwg.sa.wizard.util.PlayerStrategy

case class Gamestate (players: List[Player] = List(),
                                game_table: List[Round] = List(),
                                round_number: Int = 0,
                                active_Player_idx: Int = 0,
                                trump_Card: Card = Cards.all_cards(0),
                                serve_card: Card = Cards.all_cards(0),
                                made_tricks: List[Int] = List(),
                                playedCards: List[Card] = List(),
                                mini_starter_idx: Int = 0,
                                mini_played_counter: Int = 0) extends
  GamestateInterface with PlayerStrategy {

  def round_finished(made: Iterable[Int]): Gamestate = {
    val finished_round = game_table.last.madeTricks(made)
    val new_game_table = game_table.updated(game_table.size - 1, finished_round)
    copy(game_table = new_game_table, mini_starter_idx = (round_number + 1) % players.size, active_Player_idx = (round_number + 1) % players.size,
      round_number = round_number + 1, made_tricks = List.fill(players.size) {0}, mini_played_counter = 0)
  }

  def set_guess(guessed_tricks: Int): Gamestate = {
    val updated_guesses = game_table.last.guessed_tricks.updated(active_Player_idx, guessed_tricks)
    val updated_game_table = game_table.updated(round_number, Round(updated_guesses))
    copy(game_table = updated_game_table, active_Player_idx = (active_Player_idx + 1) % players.size)
  }

  def create_player(player_name: String): Gamestate = {
    copy(players = players.updated(active_Player_idx, Player(player_name)), active_Player_idx = (active_Player_idx + 1) % players.size)
  }

  def end_mini(played_cards_in_played_order: Iterable[Card], trump: Card, first_player_index: Int): Gamestate = {
    val winner_idx = (Cards.calcWinner(played_cards_in_played_order, trump.colour) + first_player_index) % players.size
    copy(mini_starter_idx = winner_idx, made_tricks = made_tricks.updated(winner_idx, made_tricks(winner_idx) + 1), playedCards = List(), mini_played_counter = mini_played_counter + 1, active_Player_idx = winner_idx)
  }

  def generate_Hands(round_number: Int, players: List[Player]): Gamestate = {
    var tmpCard_Tuple = (List[Card](), List[Card]())
    var n_playerList = players
    for (i <- players.indices) {
      tmpCard_Tuple = Cards.generateHand(round_number + 1, tmpCard_Tuple._2)
      val player = players(i).setHand(tmpCard_Tuple._1)
      n_playerList = n_playerList.updated(i, player)
    }
    copy(players = n_playerList, active_Player_idx = round_number % players.size, game_table = game_table.appended(Round(List.fill(players.size) {0})))
  }

  def calc_total(): List[Int] = {
    val total = Array.fill(players.size) {0}
    for (x <- players.indices) {
      game_table.foreach(round => total.update(x, total(x) + round.results(x)))
    }
    total.toList
  }

  val amount_cards = 60

  def not_last_round(round_nr : Int): Boolean = (round_nr + 1) != (amount_cards / players.size)

  def set_Trump_card(player: List[Player], round_nr: Int): Gamestate = {
    var usedCards = List[Card]()
    for (i <- player) {
      usedCards = usedCards.appendedAll(i.hand)
    }
    var trump = Cards.all_cards(0)
    if (not_last_round(round_nr)) {
      trump = Cards.generateHand(1, usedCards)._1.head
    }
    copy(trump_Card = trump)
  }

  def check_need_to_serve(played_card: Card, active_player: Player): Boolean = {
    players.indexOf(active_player) == mini_starter_idx || serve_card.num == 0 && played_card.num != 0
  }

  def playCard(played_card: Card, active_player: Player): Gamestate = {
    val updated_player = players(players.indexOf(active_player)).playCard(played_card)
    if (check_need_to_serve(played_card, active_player)) {
      copy(players = players.updated(players.indexOf(active_player), updated_player), playedCards = playedCards.appended(played_card), serve_card = played_card, active_Player_idx = (active_Player_idx + 1) % players.size)
    } else {
      copy(players = players.updated(players.indexOf(active_player), updated_player), playedCards = playedCards.appended(played_card), active_Player_idx = (active_Player_idx + 1) % players.size)
    }
  }

  def wish_trumpcard(color: String): Gamestate = copy(trump_Card = new Card_with_value(1, color))

  def set_player_amount(amount: Int): Gamestate = strategy(amount)

  def set_active_player_idx(idx: Int): Gamestate = copy(active_Player_idx = idx)

  override def strategy(amount_of_players: Int): Gamestate = {
    amount_of_players match {
      case 3 => strategy_3_players()
      case 4 => strategy_4_players()
      case 5 => strategy_5_players()
      case 6 => strategy_6_players()
    }
  }

  override def strategy_3_players(): Gamestate = copy(players = List.fill(3)(Player()), made_tricks = List.fill(3) {0})

  override def strategy_4_players(): Gamestate = copy(players = List.fill(4)(Player()), made_tricks = List.fill(4) {0})

  override def strategy_5_players(): Gamestate = copy(players = List.fill(5)(Player()), made_tricks = List.fill(5) {0})

  override def strategy_6_players(): Gamestate = copy(players = List.fill(6)(Player()), made_tricks = List.fill(6) {0})

  override def reset_player(): Gamestate = copy(players = players.updated(active_Player_idx-1 ,Player()), active_Player_idx = active_Player_idx-1)
}
