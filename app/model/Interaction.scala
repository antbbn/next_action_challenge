package model

import java.io.File

import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json._

import scala.io.Source
import scala.util.control.NonFatal

case class Interaction(user_id: Int, action: Int, timestamp: Long)

object Interaction {

  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  def parseLine(tsv: String): Interaction = {
    val row = tsv.split("\t")
    Interaction(
      user_id    = row(0).toInt,
      action    = row(1).toInt,
      timestamp = row(2).toLong
    )
  }

  def load(f: File): Seq[Interaction] = {
    val entries = Seq.newBuilder[Interaction]
    val source  = Source.fromFile(f)
    source.getLines() foreach { line =>
      try {
        entries += Interaction.parseLine(line)
      } catch {
        case NonFatal(e) =>
          log.warn(s"Failed to parse line: $line")
      }
    }
    source.close()
    entries.result()
  }

  implicit def format: Format[Interaction] = Json.format[Interaction]
}
