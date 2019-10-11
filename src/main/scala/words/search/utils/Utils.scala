package words.search.utils

import java.io.FileNotFoundException
import scala.io.Source
import scala.util.{Failure, Try}

/**
  * Created by Bondarenko on Sep, 20, 2019
  * 1:33 PM.
  * Project: words-search
  */
object Utils {
  def fromResource(path: String) =
    Try(Source.fromResource(path))
      .map(_.getLines().mkString("\n"))
      .recoverWith { case _ => Failure(new FileNotFoundException(s"$path")) }

  def linesFromResource(path: String) =
    Try(Source.fromResource(path)).map(_.getLines().toStream)

}
