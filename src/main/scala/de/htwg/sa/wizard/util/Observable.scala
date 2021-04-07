package de.htwg.sa.wizard.util

trait Observer {
  def update(bool: Boolean): Boolean
}

class Observable {

  var subscriber: Vector[Observer] = Vector()
  def add(to_add: Observer): Unit = subscriber = subscriber :+ to_add
  def remove(to_remove: Observer): Unit = subscriber = subscriber.filterNot(subscriptions => subscriptions == to_remove)
  def notify_Observer(bool: Boolean): Unit = subscriber.foreach(observer => observer.update(bool))
}
