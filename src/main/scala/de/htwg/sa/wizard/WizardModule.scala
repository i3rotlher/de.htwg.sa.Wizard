package de.htwg.sa.wizard

import com.google.inject.AbstractModule
import de.htwg.sa.wizard.control._
import de.htwg.sa.wizard.control.controllerBaseImpl.Controller
import de.htwg.sa.wizard.FileIO.File_IO_Interface
import de.htwg.sa.wizard.FileIO.JSON.Impl_JSON
import de.htwg.sa.wizard.model.gamestateComponent.GamestateBaseImpl.Gamestate
import de.htwg.sa.wizard.model.gamestateComponent.GamestateInterface
import net.codingwell.scalaguice.ScalaModule


class WizardModule extends AbstractModule with ScalaModule {
  override def configure() = {
    bind[GamestateInterface].toInstance(Gamestate())
    bind[ControllerInteface].toInstance(Controller(Gamestate()))
    bind[File_IO_Interface].toInstance(Impl_JSON())
  }
}
