package words.search.wiki

import cats.effect.Sync
import cats.implicits._
import scala.util.{Failure, Success, Try}

/**
  * Created by Bondarenko on Sep, 20, 2019
  * 4:57 PM.
  * Project: words-search
  */
trait WordsService[F[_]] {
  def usages(word: String): F[WordsUsage]
}

object WordsService {

  def apply[F[_]: Sync](wikiRepo: WikiRepo[F]): WordsService[F] = (word: String) => {
    wikiRepo
      .findWikiPages(word)
      .map { pages =>
        pages.map(_.text).flatMap {
          _.split('.').filter(_.toLowerCase.contains(word.toLowerCase))
        }
      }
      .map(usages => WordsUsage(word, usages.toList))

  }

  def apply[F[_]: Sync](
      source: String => Try[Stream[String]]
  ): WordsService[F] = new WordsService[F] {
    def usages(word: String): F[WordsUsage] = {

      val firstUsage = source(word)
        .map { lines =>
          lines
            .flatMap(
              _.split('.')
                .filter(_.toLowerCase.contains(word.toLowerCase))
                .map(_.trim)
            )

        }

      firstUsage match {
        case Failure(exception) => WordsUsage(word, Nil).pure[F]
        case Success(value)     => WordsUsage(word, value.toList).pure[F]
      }

    }
  }
}
