package de.htwg.se.wizard.aview.gui
import java.awt.Color

import de.htwg.se.wizard.control._
import de.htwg.se.wizard.control.controllerBaseImpl.{Wizard_trump, card_not_playable, game_over, game_started, get_Amount, mini_over, next_guess, next_player_card, player_create, round_over, set_Wizard_trump, start_round}
import de.htwg.se.wizard.model.cardsComponent.Card

import scala.swing._
import scala.swing.event._
import scala.util.{Failure, Success, Try}

class SwingGUI(controller: ControllerInteface) extends Frame {

  listenTo(controller)
  centerOnScreen()
  override def closeOperation(): Unit = {
    System.exit(-1)
  }
  title = "Wizard"

  var state: Event = new game_started

  reactions+= {
    case event: game_started => game_start()
    case event: get_Amount => state = event
    case event: start_round => start_round()
    case event: Wizard_trump => wizard_trump()
    case event: set_Wizard_trump => state = event
    case event: player_create =>
      set_name(controller.active_player_idx() + 1)
      state = event
    case event: next_guess =>
      guess()
      state = event
    case event: next_player_card =>
      play_card()
      state = event
    case event: card_not_playable =>
      println("This card is not playable right now!\n Choose a different number!");
    case event: mini_over =>
      println("Trick won by " + controller.get_mini_winner().getName + "!");
    case event: round_over =>
      println(controller.getGamestate().game_table);
    case event: game_over =>
      println("Game Over!")
      println(controller.getGamestate().calc_total())
      state = event
    case ButtonClicked(b) =>
      if (b.name == "load") {
        controller.load()
      } else if (b.name == "save") {
        controller.save(state)
      } else {
        println(b.name);processInput(b.name)
      }
    case EditDone(s) => if (s.text != "Enter your name here ..." && s.text != "Enter your guess here ..." && s.text != "Enter your cardnumber here ...") {
      println(s.text)
      processInput(s.text)
      s.text = "Enter your name here ..." // zurücksetzten auf Enter ... weil sonst bei contents wechsel auf guess texfeld erneut mit der alten eingabe geschickt wurde
    }
  }

  def processInput(input: String) : Unit = {
    state match {
      case event: get_Amount => check_Amount(input)
      case event: player_create => create_player(input)
      case event: set_Wizard_trump => check_trump_wish(input)
      case event: next_guess => get_guess(input)
      case event: next_player_card => get_card(input)
      case _ => println("state unimplemented: " + state)
    }
  }

  def game_start(): Unit = {
    val amount_panel = new FlowPanel {
      contents += new Label("How many players ?")
      val button3 = new Button("3 Players") {
        name = "3"
      }
      val button4 = new Button("4 Players") {
        name = "4"
      }
      val button5 = new Button("5 Players"){
        name = "5"
      }
      val button6 = new Button("6 Players"){
        name = "6"
      }
      val buttonload = new Button("Load Game"){
        name = "load"
      }
      contents+=button3
      contents+=button4
      contents+=button5
      contents+=button6
      contents+=buttonload
    }
    listenTo(amount_panel.button3)
    listenTo(amount_panel.button4)
    listenTo(amount_panel.button5)
    listenTo(amount_panel.button6)
    listenTo(amount_panel.buttonload)

    contents = amount_panel
    controller.publish(new get_Amount)
  }

  // ---------------------------------------------------------------------------------------------------------

  var wish_trump_panel = new FlowPanel {
    val button_red = new Button("Red") {
      background = Color.RED
      name = "red"
    }
    val button_green = new Button("Green") {
      background = Color.GREEN
      name = "green"
    }
    val button_blue = new Button("Blue"){
      background = Color.BLUE
      name = "blue"
    }
    val button_yellow = new Button("Yellow"){
      background = Color.YELLOW
      name = "yellow"
    }
  }

  def wizard_trump(): Unit = {

    wish_trump_panel = new FlowPanel {
      val label = new Label("A wizard has been drawn as trump! " + controller.get_player(controller.active_player_idx()).getName + " which color do you want to be trump?")
      val trump_label = new Label("Trump card:")
      var trump = Card_panel(controller.getGamestate().trump_Card)
      val hand_label = new Label("Your cards:")
      var hand = new Hand_panel(controller.getGamestate().players(controller.active_player_idx()).getHand)

      val button_red = new Button("Red") {
        background = Color.RED
        name = "red"
      }
      val button_green = new Button("Green") {
        background = Color.GREEN
        name = "green"
      }
      val button_blue = new Button("Blue"){
        background = Color.BLUE
        name = "blue"
      }
      val button_yellow = new Button("Yellow"){
        background = Color.YELLOW
        name = "yellow"
      }

      contents+=label
      contents+=trump_label
      contents+=trump
      contents+=hand_label
      contents+=hand
      contents+=button_red
      contents+=button_green
      contents+=button_blue
      contents+=button_yellow
    }

    listenTo(wish_trump_panel.button_green)
    listenTo(wish_trump_panel.button_red)
    listenTo(wish_trump_panel.button_blue)
    listenTo(wish_trump_panel.button_yellow)

    contents = new BorderPanel {
      add(wish_trump_panel, BorderPanel.Position.Center)
      add(new Table_panel(controller.getGamestate()), BorderPanel.Position.East)
    }

  }

  //---------------------------------------------------------------------------------------------------------

  // ------------------------------------------------------------------------------------------

  var set_guess_panel = new FlowPanel {
    var text_field = new TextField("Enter your guess here ...", 14)
    var save_button = new Button("Save Game") {name = "save"}
    contents+=text_field
  }

  def guess(): Unit = {

    set_guess_panel = new FlowPanel {
      val label = new Label(controller.get_player(controller.active_player_idx()).getName + " how many tricks are you going to make?")
      val trump_label = new Label("Trump card:")
      var trump = Card_panel(controller.getGamestate().trump_Card)
      val hand_label = new Label("Your cards:")
      var hand = new Hand_panel(controller.getGamestate().players(controller.active_player_idx()).getHand)
      var text_field = new TextField("Enter your guess here ...", 14)
      var save_button = new Button("Save Game") {
        name = "save"
      }

      contents+=label
      contents+=trump_label
      contents+=trump
      contents+=hand_label
      contents+=hand
      contents+=text_field
      contents+=save_button
    }
    listenTo(set_guess_panel.text_field)
    listenTo(set_guess_panel.save_button)
    contents = new BorderPanel {
      add(set_guess_panel, BorderPanel.Position.Center)
      add(new Table_panel(controller.getGamestate()), BorderPanel.Position.East)
    }
  }

  // ------------------------------------------------------------------------------------------

  //------------------------------------------------------------------------------------------

  var playing_panel = new FlowPanel {
    var text_field = new TextField("Enter your cardnumber here ...", 14)
    contents+=text_field
  }

  def play_card(): Unit = {

    playing_panel = new FlowPanel {
      val label = new Label(controller.get_player(controller.active_player_idx()).getName + " which card to you want to play?")
      val trump_label = new Label("Trump card:")
      var trump = Card_panel(controller.getGamestate().trump_Card)
      val hand_label = new Label("Your cards:")
      var hand = new Hand_panel(controller.getGamestate().players(controller.active_player_idx()).getHand)
      var text_field = new TextField("Enter your cardnumber here ...", 14)

      contents+=label
      contents+=trump_label
      contents+=trump
      contents+=hand_label
      contents+=hand
      contents+=text_field
    }
    listenTo(playing_panel.text_field)
    contents = new BorderPanel {
      add(playing_panel, BorderPanel.Position.Center)
      add(new Table_panel(controller.getGamestate()), BorderPanel.Position.East)
    }
  }


  //------------------------------------------------------------------------------------------

  // ------------------------------------------------------------------------------------------

  val set_name_panel = new FlowPanel {
    val label = new Label("Player " + "X" + " whats your Name ?")
    contents += label
    val text_field = new TextField("Enter your name here ...", 14)
    val undo_previous = new Button("\u2190") {
      name = "r"
    }
    val redo_change = new Button("\u2192") {
      name = "y"
    }
    contents += undo_previous
    contents += text_field
    contents += redo_change
  }

  listenTo(set_name_panel.undo_previous)
  listenTo(set_name_panel.text_field)
  listenTo(set_name_panel.redo_change)

  def set_name(activeplayer_idx: Int): Unit = {
    set_name_panel.label.text ="Player " + activeplayer_idx + " whats your Name ?"
    contents=set_name_panel
  }

  // ------------------------------------------------------------------------------------------

  def create_player(input: String): Unit = {
    input match {
      case "y" => controller.redo_player()
      case "r" => controller.undo_player()
      case _ => controller.create_player(input)
    }
  }



  def check_Amount(input: String): Int = {
    if (!List("3", "4", "5", "6").contains(input)) {
      println("There may only be 3,4,5 or 6 players!")
      return -1
    }
    controller.set_player_amount(Some(input.toInt))
    input.toInt
  }

  def start_round() : Unit = {
    //    println("- - - - - Round " + (controller.game.round_number+1) + " started - - - - -")
    //    println("Generating hands . . .")
    //    println("Generating trumpcard . . .\n\n\n")
  }

  def check_trump_wish(input: String): Boolean = {
    //    if(!List("red","green","blue","yellow").contains(input)) {
    //      println("You may only choose one of these colors red,blue,yellow,green")
    //      return false
    //    }
    //    controller.wish_trump(input)
    true
  }

  def toInt(s: String): Try[Int] = Try(Integer.parseInt(s.trim))

  def get_guess(input: String):Boolean = {
    val integer = toInt(input)
    integer match {
      case Failure(_) => println("Insert a correct number!");false
      case Success(_) =>
        if (integer.get < 0) {
          println("Insert a correct number (>= 0)!")
          false
        } else {
          controller.set_guess(integer.get)
          true
        }
    }
  }

  def get_card(input: String): Unit = {
    val active_player = controller.get_player(controller.active_player_idx())
    var list = List[String]()
    for (x <- 1 to active_player.getHand.size) {
      list = list :+ x.toString
    }
    if (!list.contains(input)) {
      println("Insert a valid card number!")
    } else {
      controller.play_card(active_player.getHand(input.toInt-1))
    }
  }

  def get_card_face_path(card: Card): String = {
    if (card.colour.contains("none")) {
      return "C:\\Users\\maxek\\Desktop\\AIN\\Semester 3\\Software Engeneering\\de.htwg.se.Wizard\\Card_faces\\" + (card.colour.substring(card.colour.indexOf("(")+1,card.colour.indexOf("(")+2) + card.num) + ".png"
    }
    "C:\\Users\\maxek\\Desktop\\AIN\\Semester 3\\Software Engeneering\\de.htwg.se.Wizard\\Card_faces\\" + (card.colour.substring(0,1) + card.num) + ".png"
  }

  visible = true
}