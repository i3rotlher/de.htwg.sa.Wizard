package de.htwg.sa.wizard.model

import de.htwg.sa.wizard.util.{Observable, Observer}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class ObservableTest extends AnyWordSpec {
  "An Observable" should {
    val observable = new Observable
    val observer = new Observer {
      var updated: Boolean = false
      def isUpdated: Boolean = updated
      override def update(boolean: Boolean): Boolean = {updated = true; updated}
    }
    "add an Observer" in {
      observable.add(observer)
      observable.subscriber should contain (observer)
    }
    "notify an Observer" in {
      observer.isUpdated should be(false)
      observable.notify_Observer(bool = true)
      observer.isUpdated should be(true)
    }
    "remove an Observer" in {
      observable.remove(observer)
      observable.subscriber should not contain (observer)
    }
  }
}
