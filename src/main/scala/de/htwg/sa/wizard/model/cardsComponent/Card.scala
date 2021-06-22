package de.htwg.sa.wizard.model.cardsComponent

abstract case class Card(num: Int, colour: String) {
  def get_String: String
  override def toString:String = get_String
}
class Card_with_value(num: Int, colour: String) extends Card(num, colour) {
    override def get_String: String = "(%s, %s)".format(num.toString, colour)
  }
class Card_wizard(num: Int, colour: String) extends Card(num, colour) {
    override def get_String: String = "(%s, %s)".format("Zauberer", colour)
  }
class Card_fool(num: Int, colour: String) extends Card(num, colour){
    override def get_String: String = "(%s, %s)".format("Narr", colour)
  }

