package services

import model.Interaction
import org.slf4j.{Logger, LoggerFactory}

object EvaluationService {

  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  val test: Seq[Interaction] = Interaction.load(play.Play.application().getFile("test.csv"))

  def simpleEval(): EvalResults = {
    val results = test map { i =>
      val predicted = NextActionPredictor.CurrentStrategy.predictNextAction(i.user_id, i.timestamp)
      NextActionPredictor.CurrentStrategy.storeFeedback(i)
      predicted == i.action
    }
    val n = test.size.toDouble
    EvalResults(results.count(_ == true).toDouble / n, results.count(!_) / n)
  }
}

final case class EvalResults(fractionCorrect: Double, fractionWrong: Double)
