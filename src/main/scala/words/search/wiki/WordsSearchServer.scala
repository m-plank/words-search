package words.search.wiki

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, CORSConfig, Logger}
import org.http4s.server.staticcontent.{FileService, _}
import words.search.utils.Logging
import words.search.wiki.mongo.Mongo
import scala.concurrent.duration._

object WordsSearchServer extends Logging {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

    info(s"STARTING SERVER...")

    def httpApp = {
      val wordsService = WordsService(Mongo.wikiRepo[F])

      val methodConfig = CORSConfig(
        anyOrigin = true,
        anyMethod = false,
        allowedMethods = Some(Set("GET", "POST")),
        allowCredentials = true,
        maxAge = 1.day.toSeconds
      )

      Logger.httpApp(true, false) {
        (
          CORS(WikiRoutes(wordsService), methodConfig) <+>
            fileService(FileService.Config("./public/assets"))
        ).orNotFound
      }

    }

    for {
      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(httpApp)
        .serve
    } yield exitCode
  }.drain
}
