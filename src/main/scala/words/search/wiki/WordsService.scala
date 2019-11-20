package words.search.wiki

import cats.effect.Sync
import cats.implicits._

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

}
