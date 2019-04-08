package services

import model.{Interaction, Profile}

import scala.collection.mutable
import scala.util.Random

trait NextActionPredictor {

  /**
    * Predicting the next action.
    *
    * @param userId ID of the user for whom the next action is predicted
    * @param timestamp time when the user performs her next action
    * @return the type of action that will most likely be performed
    */
  def predictNextAction(userId: Int, timestamp: Long): Int

  /**
    * Storing feedback (e.g. for learning about the user).
    *
    * @param i the interaction that was actually performed
    * @return feedback was / was not successfully received
    */
  def storeFeedback(i: Interaction): Boolean
}

object NextActionPredictor {
  val interactions: Seq[Interaction] = Interaction.load(play.Play.application().getFile("train.csv"))
  val profiles:     Seq[Profile]     = Profile.load(play.Play.application().getFile("profiles.csv"))

  //TODO: replace `new DummyPredictor(..)` with your NextActionPredictor
  //Note: you can also write your predictor in Java...
  //val CurrentStrategy: NextActionPredictor = new NextActionPredictorJava()
  val CurrentStrategy: NextActionPredictor = new DummyPredictor(interactions, profiles)
}


class DummyPredictor(train: Seq[Interaction], profiles: Seq[Profile]) extends NextActionPredictor {

  val feedback: mutable.Map[Int, Int] = mutable.Map.empty[Int, Int]
  train.foreach(i => storeFeedback(i))

  val userDetails: Map[Int, Profile] = profiles.map(p => p.user_id -> p).toMap

  val rand: Random = new Random(seed = 42)

  override def predictNextAction(userId: Int, timestamp: Long): Int = {
    feedback.getOrElse(userId, rand.nextInt(4) + 1)
  }

  def storeFeedback(i: Interaction): Boolean = {
    //simply remember the last action of the user:
    feedback.put(i.user_id, i.action)
    true
  }
}