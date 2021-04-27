package de.htwg.sa.wizard.FileIO

trait File_IO_Interface {
  def load: String
  def save(JSONstring: String):Unit
}
