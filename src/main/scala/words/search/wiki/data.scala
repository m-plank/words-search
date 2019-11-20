package words.search

import cats.Applicative
import cats.effect.Sync
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

/**
 * Created by Bondarenko on Sep, 20, 2019
 * 5:06 PM.
 * Project: words-search
 */
package object wiki {

  case class WordsUsage(word: String, usages: List[String])

  implicit val usageDecoder: Decoder[WordsUsage] = deriveDecoder
  implicit val usageEncoder: Encoder[WordsUsage] = deriveEncoder

  implicit def usageEntityDecoder[F[_]: Sync]: EntityDecoder[F, WordsUsage] = jsonOf
  implicit def usageEntityEncoder[F[_]: Applicative]: EntityEncoder[F, WordsUsage] =
    jsonEncoderOf[F, WordsUsage]
}
