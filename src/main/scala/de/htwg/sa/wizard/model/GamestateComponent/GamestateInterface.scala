package de.htwg.sa.wizard.model.GamestateComponent.GamestateBaseImpl

import de.htwg.sa.wizard.model.cardsComponent.Card
import de.htwg.sa.wizard.model.playerComponent.PlayerBaseImpl.Player
import de.htwg.sa.wizard.util.PlayerStrategy

trait GamestateInterface extends PlayerStrategy {

  // variablen ----
  def players: List[Player]

  def game_table: List[Round]

  def round_number: Int

  def active_Player_idx: Int

  def trump_Card: Card

  def serve_card: Card

  def made_tricks: List[Int]

  def playedCards: List[Card]

  def mini_starter_idx: Int

  def mini_played_counter: Int


  def round_finished(made: Iterable[Int]): GamestateInterface

  def set_guess(guessed_tricks: Int): GamestateInterface

  def create_player(player_name: String): GamestateInterface

  def end_mini(played_cards_in_played_order: Iterable[Card], trump: Card, first_player_index: Int): GamestateInterface

  def generate_Hands(round_number: Int, players: List[Player]): GamestateInterface

  def calc_total(): List[Int]

  def set_Trump_card(player: List[Player], round_nr: Int): GamestateInterface

  def playCard(played_card: Card, active_player: Player): GamestateInterface

  def wish_trumpcard(color: String): GamestateInterface

  def set_player_amount(amount: Int): GamestateInterface

  def set_active_player_idx(idx: Int): GamestateInterface

  def reset_player(): GamestateInterface

  override def strategy(amount_of_players: Int): GamestateInterface

  override def strategy_3_players(): GamestateInterface

  override def strategy_4_players(): GamestateInterface

  override def strategy_5_players(): GamestateInterface

  override def strategy_6_players(): GamestateInterface
}
