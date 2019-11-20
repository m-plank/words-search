package words.search.wiki

import org.http4s.syntax.KleisliSyntax
import scala.io.Source

/**
 * Created by Bondarenko on Oct, 17, 2019
 * 14:01.
 * Project: words-search
 */
class WikiItTest extends org.specs2.mutable.Specification with KleisliSyntax with RunningApp with ClientApi {

  "words-search app" >> {
    "returns list of usages for a phrase" >> {
      get("heavy wood beams") must beEqualTo(
        """{"word":"heavy wood beams","usages":[" Atop the last courses of brick, bond beams made of heavy wood beams or modern reinforced concrete are laid to provide a horizontal bearing plate for the roof beams and to redistribute lateral earthquake loads to shear walls more able to carry the forces"]}""".stripMargin
      )
    }
  }

  def get(words: String) =
    Source
      .fromURL(s"http://localhost:8080/api/get-word-usages/${words.replaceAll(" ", "%20")}")
      .getLines()
      .mkString("\n")

}
