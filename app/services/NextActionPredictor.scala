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

  val CurrentStrategy: NextActionPredictor = new AnalysisPredictor(interactions, profiles)
}

class AnalysisPredictor(train: Seq[Interaction], profiles: Seq[Profile]) extends NextActionPredictor {

  val feedback: mutable.Map[Int, Int] = mutable.Map.empty[Int, Int]
  val changes: mutable.Map[Int, Int] = mutable.Map.empty[Int, Int]
  val counts: mutable.Map[Int, Int] = mutable.Map.empty[Int, Int]
  val timestamps: mutable.Map[Int, Long] = mutable.Map.empty[Int, Long]

  train.foreach(i => storeFeedback(i))

  val userDetails: Map[Int, Profile] = profiles.map(p => p.user_id -> p).toMap

  override def predictNextAction(userId: Int, timestamp: Long): Int = {
    var lastFeedback = feedback.getOrElse(userId,0) 
    var lastTimestamp = timestamps.getOrElse(userId,timestamp-1)
    var count_changes = changes.getOrElse(userId,0)
    var count = counts.getOrElse(userId,0)
    
    var nextFeedback = 3 // We start with three as default
    if (lastFeedback > 0) {
      nextFeedback = lastFeedback  // By default we repeat
      if (count > 1 & count_changes/count > 0.5) { // unleass Lots of changes in the past
        if (lastFeedback == 1| lastFeedback == 2 |lastFeedback == 3 ) {
          nextFeedback = 4
        } else {
          nextFeedback = 3
        }
      } else { 
        if (((timestamp - lastTimestamp) > 1500) &  //  or a long time passed
          (lastFeedback == 1| lastFeedback == 2 |lastFeedback == 5 )) {
            nextFeedback = 4
        }
      }
    }
    nextFeedback
  }

  def storeFeedback(i: Interaction): Boolean = {
    //simply remember the last action of the user:
    val prev = feedback.put(i.user_id, i.action)
    timestamps.put(i.user_id, i.timestamp)

    val count = counts.getOrElse(i.user_id,0)
    counts.put(i.user_id,count+1)

    val change = changes.getOrElse(i.user_id,0)
    if (prev.getOrElse(0) == i.action) {
      changes.put(i.user_id,change+1)
    }     
    
    true
  }
}

class NotSoDummyPredictor(train: Seq[Interaction], profiles: Seq[Profile]) extends NextActionPredictor {

  val feedback: mutable.Map[Int, Int] = mutable.Map.empty[Int, Int]
  train.foreach(i => storeFeedback(i))

  val userDetails: Map[Int, Profile] = profiles.map(p => p.user_id -> p).toMap

  val rand: Random = new Random(seed = 42)

  override def predictNextAction(userId: Int, timestamp: Long): Int = {
    feedback.getOrElse(userId, 3)
  }

  def storeFeedback(i: Interaction): Boolean = {
    //simply remember the last action of the user:
    feedback.put(i.user_id, i.action)
    true
  }
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
