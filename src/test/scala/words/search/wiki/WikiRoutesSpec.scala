package words.search.wiki

import cats.effect.{ContextShift, IO}
import org.http4s.syntax.KleisliSyntax
import org.http4s.{Method, Request, Uri, _}
import scala.concurrent.ExecutionContext

/**
  * Created by Bondarenko on Sep, 20, 2019
  * 5:26 PM.
  * Project: words-search
  */
class WikiRoutesSpec extends org.specs2.mutable.Specification with KleisliSyntax {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  "WikiRoutes" >> {
    "return bad request for empty query word" >> {
      usages(uri"/api/get-word-usages/").status must beEqualTo(Status.BadRequest)
    }

    "return list of word usages for a given phrase" >> {
      val usage = usages(uri"/api/get-word-usages/nail%20down").as[WordsUsage].unsafeRunSync()

      usage.word must beEqualTo("nail down")
      usage.usages must beEqualTo(
        List(
          """I am in Snowdonia, one of the best places in the UK to nail down 
            |exactly what it is that makes your toes tingle with a heady mixture of excitement and fear""".stripMargin
        )
      )

    }

    "return list of word usages for a given phrase ignoring case" >> {
      val usage = usages(uri"/api/get-word-usages/Nail%20Down").as[WordsUsage].unsafeRunSync()

      usage.word must beEqualTo("Nail Down")
      usage.usages must beEqualTo(
        List(
          """I am in Snowdonia, one of the best places in the UK to nail down 
            |exactly what it is that makes your toes tingle with a heady mixture of excitement and fear""".stripMargin
        )
      )

    }

    "return lists of word usage for a single word" >> {
      val usage = usages(uri"/api/get-word-usages/Snowdonia").as[WordsUsage].unsafeRunSync()

      usage.word must beEqualTo("Snowdonia")
      usage.usages must contain(
        """I am in Snowdonia, one of the best places in the UK to nail down 
          |exactly what it is that makes your toes tingle with a heady mixture of excitement and fear""".stripMargin
      )
      usage.usages must contain(
        """Snowdonia’s old slate quarries, once an eyesore, are being reinvented too""".stripMargin
      )

    }

  }

  private val wikiRepo = new WikiRepo[IO] {
    def findWikiPages(words: String): IO[Stream[WikiPage]] = IO.pure {
      Stream(
        WikiPage(
          "Some title",
          """I am in Snowdonia, one of the best places in the UK to nail down 
            |exactly what it is that makes your toes tingle with a heady mixture of excitement and fear. 
            |With its mountains, rivers and coast, this region has been attracting adventurers ever 
            |since Victorian times when the first rock climbers came.""".stripMargin
        ),
        WikiPage(
          "Some other title",
          """Snowdonia’s old slate quarries, once an eyesore, are being reinvented too.
            | Next day we try Zipworld at Penrhyn quarry, home to Velocity 2 – 
            | the fastest zipline in the world – plus a new karting track.""".stripMargin
        )
      )
    }
  }

  private def usages(uri: Uri) = {
    val request = Request[IO](Method.GET, uri)
    WikiRoutes(WordsService[IO](wikiRepo)).orNotFound(request).unsafeRunSync()
  }
}
