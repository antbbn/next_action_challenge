package controllers

import model.Interaction
import org.slf4j.{Logger, LoggerFactory}
import services.NextActionPredictor
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.concurrent.Akka
import play.api.Play.current

import scala.concurrent.ExecutionContext

object PredictionController extends Controller {

  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  implicit val ec: ExecutionContext = Akka.system.dispatcher

  def predict(userId: Int, timestamp: Int): Action[AnyContent] = Action { implicit request =>
    Ok(Json.obj("predicted_action" -> NextActionPredictor.CurrentStrategy.predictNextAction(userId, timestamp)))
  }

  def storeFeedback: Action[Interaction] = Action(parse.json[Interaction]) { implicit request =>
    Ok(Json.obj("feedback_received" -> NextActionPredictor.CurrentStrategy.storeFeedback(request.body)))
  }
}
