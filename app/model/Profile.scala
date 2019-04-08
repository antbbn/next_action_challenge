package model

import java.io.File

import org.slf4j.{Logger, LoggerFactory}

import scala.io.Source
import scala.util.control.NonFatal

case class Profile(user_id: Int, job_seeker_status: Int, number_contacts: Int)

object Profile {

  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  def apply(tsv: String): Profile = {
    val row = tsv.split("\t")
    Profile(
      user_id          = row(0).toInt,
      job_seeker_status = row(1).toInt,
      number_contacts  = row(2).toInt
    )
  }

  def load(f: File): Seq[Profile] = {
    val entries = Seq.newBuilder[Profile]
    val source  = Source.fromFile(f)
    source.getLines() foreach { line =>
      try {
        entries += Profile(line)
      } catch {
        case NonFatal(e) =>
          log.warn(s"Failed to parse line: $line")
      }
    }
    source.close()
    entries.result()
  }
}
