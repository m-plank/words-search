package words.search.wiki

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    WordsSearchServer.stream[IO].compile.drain.as(ExitCode.Success)
}
