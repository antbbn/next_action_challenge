package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.concurrent.Akka
import play.api.Play.current
import services.EvaluationService

import scala.concurrent.ExecutionContext

object EvaluationController extends Controller {

  implicit val ec: ExecutionContext = Akka.system.dispatcher

  def evaluate(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.eval(EvaluationService.simpleEval()))
  }
}
