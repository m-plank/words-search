package words.search.wiki

/**
 * Created by Bondarenko on Oct, 10, 2019
 * 11:16 PM.
 * Project: words-search
 */
case class WikiPage(title: String, text: String)

trait WikiRepo[F[_]] {
  def findWikiPages(words: String): F[Stream[WikiPage]]
}
