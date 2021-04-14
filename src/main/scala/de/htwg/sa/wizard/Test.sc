import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import scala.concurrent.Future
import scala.util.{Failure, Success}
implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
// needed for the future flatMap/onComplete in the end
implicit val executionContext = system.executionContext

val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/JSON"))

responseFuture
  .onComplete {
    case Success(res) => println(res)
    case Failure(_)   => sys.error("something wrong")
  }