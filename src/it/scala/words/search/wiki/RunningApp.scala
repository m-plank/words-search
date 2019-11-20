package words.search.wiki

import cats.effect.{ContextShift, IO, Timer}
import java.util.concurrent._
import scala.concurrent.ExecutionContext

/**
 * Created by Bondarenko on Oct, 17, 2019
 14:02.
 Project: words-search
 */
trait RunningApp extends Context {

  def run[A](f: => A) = IO.delay(f).start.unsafeRunAsyncAndForget()

  val _ = run(Main.main(Array.empty[String]))
}

trait ClientApi extends Context {
  import org.http4s.client._
  import org.http4s.client.blaze._
  import scala.concurrent.ExecutionContext.global

  implicit val timer: Timer[IO] = IO.timer(global)

  lazy val blockingEC = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(1))
  lazy val httpClient: Client[IO] = JavaNetClientBuilder[IO](blockingEC).create

//  def query(words: String) =
//    httpClient.expect[String](s"http://localhost:8080/api/get-word-usages/${words.replaceAll(" ", "%20")}")

  def query(words: String) =
    BlazeClientBuilder[IO](global).resource.use { client =>
      client.expect[String](s"http://localhost:8080/api/get-word-usages/${words.replaceAll(" ", "%20")}")
    }

}

trait Context {
  import scala.concurrent.ExecutionContext.global

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
}
