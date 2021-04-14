package de.htwg.sa.wizard.FileIO

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import de.htwg.sa.wizard.FileIO.JSON.Impl_JSON

case object FileIOService {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    // load (get) / save (post) von xml und html rein
    val route =
      concat (
              get {
                path("JSON") {
                  complete(HttpEntity(ContentTypes.`application/json`, Impl_JSON().load_JSON()))
                }
              },
              post {
                path("JSON") {
                  entity(as [String]) { game =>
                    Impl_JSON().save(game)
                    println("GAME SAVED")
                    complete("game saved")
                  }
                }
              }

      )
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"FileIO Server online at http://localhost:8080/\nPress RETURN to stop...")
  }
}
