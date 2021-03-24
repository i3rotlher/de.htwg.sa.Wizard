package de.htwg.se.wizard

import com.google.inject.AbstractModule
import de.htwg.se.wizard.control._
import de.htwg.se.wizard.control.controllerBaseImpl.Controller
import de.htwg.se.wizard.model.FileIO.File_IO_Interface
import de.htwg.se.wizard.model.FileIO.JSON.Impl_JSON
import de.htwg.se.wizard.model.FileIO.XML.Impl_XML
import de.htwg.se.wizard.model.gamestateComponent.GamestateBaseImpl.Gamestate
import de.htwg.se.wizard.model.gamestateComponent.GamestateInterface
import net.codingwell.scalaguice.ScalaModule


class WizardModule extends AbstractModule with ScalaModule {
  override def configure() = {
    bind[GamestateInterface].toInstance(Gamestate())
    bind[ControllerInteface].toInstance(Controller(Gamestate(), Impl_JSON()))
    bind[File_IO_Interface].toInstance(Impl_XML())
  }
}
