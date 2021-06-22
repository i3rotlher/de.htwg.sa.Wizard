package de.htwg.sa.wizard.FileIO
import de.htwg.sa.wizard.FileIO.JSON.Impl_JSON
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class Impl_JSONTest extends AnyWordSpec {
  "an JSON" when {
    "saving and reloading the game" should {
      "have the same gamestate" in {
        val json: File_IO_Interface = Impl_JSON()
        //json.save("Test: JSON")
        //json.load shouldBe "Test: JSON"
      }
    }
  }
}


