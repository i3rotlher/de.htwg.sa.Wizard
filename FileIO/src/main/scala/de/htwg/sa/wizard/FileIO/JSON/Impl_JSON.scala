package de.htwg.sa.wizard.FileIO.JSON


import de.htwg.sa.wizard.FileIO.File_IO_Interface

case class Impl_JSON() extends File_IO_Interface {

  def load(): String = {
    val file = scala.io.Source.fromFile("gamestate.json")
    try file.mkString finally file.close()
  }

  def save(gamestate_json: String): Unit = {
    import java.io._
    val print_writer = new PrintWriter(new File("gamestate.json"))
    print_writer.write(gamestate_json)
    print_writer.close()
  }
}
