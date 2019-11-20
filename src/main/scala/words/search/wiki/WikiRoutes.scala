package words.search.wiki

import cats.effect.{ContextShift, Sync}
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, StaticFile}
import words.search.utils.Logging
import java.io.File
import java.util.concurrent._
import scala.concurrent.ExecutionContext

/**
 * Created by Bondarenko on Sep, 20, 2019
 * 4:58 PM.
 * Project: words-search
 */
object WikiRoutes extends Logging {

  val blockingEc = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))

  def apply[F[_]: Sync: ContextShift](wordsService: WordsService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "api" / "get-word-usages" / word =>
        if (word.isEmpty)
          BadRequest("Empty query!")
        else {
          Sync[F].suspend(wordsService.usages(word)).attempt.flatMap {
            _ match {
              case Right(value) => Ok(value)
              case Left(err) =>
                error(err)
                BadRequest("Something goes wrong")
            }
          }
        }

      case GET -> Root / "index.html" =>
        StaticFile
          .fromFile(new File("public/index.html"), blockingEc)
          .getOrElseF(NotFound())

    }

  }
}
