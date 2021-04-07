package de.htwg.sa.wizard.model

import de.htwg.sa.wizard.control.controllerBaseImpl.{Wizard_trump, card_not_playable, game_over, game_started, get_Amount, guesses_set, mini_over, name_ok, next_guess, next_player_card, player_create, round_over, round_started, set_Wizard_trump, start_round}
import de.htwg.sa.wizard.control.controllerBaseImpl.game_over
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class StateTest extends AnyWordSpec {{
  "all state tests" should {
    "be callable classes" in {
      new name_ok
      new set_Wizard_trump
      new game_started
      new get_Amount
      new player_create
      new round_started
      new start_round
      new Wizard_trump
      new next_guess
      new next_player_card
      new round_over
      new card_not_playable
      new guesses_set
      new mini_over
      new game_over

    }
  }
}
}
