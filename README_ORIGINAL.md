Predicting the next action of a user
====================================

People can perform different types of actions on XING. For example, they can search for jobs,
connect with other XING users, attend events, browse details about companies and use XING
to create a profile.
In this assignment, we would like to develop a lightweight predictor that allows
for estimating what kind of action a user will perform next.

To that end, we provide two types of datasets:

- `interactions` (cf. _train.csv_ and _test.csv_): the interactions that a user performed
  + `userId`: the ID of the user that performed the interaction
  + `action`: the actual action that the user performed:
    - 1 = browsing details about companies
    - 2 = browsing events (MeetUps, conferences, etc.)
    - 3 = searching for contacts or interacting with other XING users
    - 4 = browsing jobs
    - 5 = editing the profile (e.g. adding skills, updating the CV, etc.)
  + `timestamp` = the timestamp when the interaction was performed (this does not refer to an actual timestamp, but allows for bringing the interactions into a sequential order)
- `profiles` (cf. _profiles.csv)_: some basic details about the user
  + `userId`: the ID of the user
  + `job_seeking_status`: indicates whether the user states that she is currently searching for a job
    - 1 = no
    - 2 = open for offers
    - 3 = yes
  + `number_contacts` = the number of contacts that the user has


### Task
Your task is to analyze the given dataset(s) and:

1. design a lightweight algorithm that allows for predicting the next action of a user; and
2. implement your algorithm in Scala or Java (as you prefer) and modify [NextActionPredictor.scala](app/services/NextActionPredictor.scala#L35)) so that it uses your implementation
3. explain your approach and the main insights you have gathered

## Remarks

- We do not expect that you find a sophisticated, optimal solution. A lightweight, simple algorithm is perfectly fine. However, we expect that you justify why the algorithm makes sense with respect to the given data.
- You are responsible for managing your time (e.g. if you want to invest 1 hour that's fine or if you want to invest 10 hours that's also fine). We expect that you are sending us your solution within 7 days from receiving the case.  
- For the first part of the assignment (algorithm design), feel free to use the programming language of your choice (e.g. in case you decide to use Python or R that's perfectly fine). 
- Please send us your code/descriptions that you used for the algorithm design, the modified Scala/Java code that implements your algorithm and also a brief explanation of your approach and the main insights you have gathered while working on the case
- We will then analyze all the documents you sent us and depending on that will reach out to you again within a few days and set up another meeting(either VC or on-site) to discuss in more detail your solution. 


## Some pointers for the implementation part

- the application can be built and run with [sbt](http://www.scala-sbt.org/)
- the application currently consists primarily of the following classes:
  + [controllers.EvaluationController](app/controllers/EvaluationController.scala): the evaluation controller (does not need to be modified)
  + [controllers.PredictionController](app/controllers/PredictionController.scala): the controller for `/api/predict/...` (does not need to be modified)
  + [model.Interaction](app/model/Interaction.scala): the case class that models an interaction (does not need to be modified)
  + [model.Profile](app/model/Profile.scala): the case class that models details about the user (does not need to be modified)
  + [services.EvaluationService](app/services/EvaluationService.scala): lightweight service that evaluates the predictor (does not need to be modified)
  + **[services.NextActionPredictor](app/services/NextActionPredictor.scala):** the actual class that should be modified by you
  + [services.NextActionPredictorJava](app/services/NextActionPredictorJava.java): if you prefer to use Java then feel free to modify/use this class to implement your algorithm
- when you start the application via `sbt run` then you can go to [http://localhost:9000](http://localhost:9000) to see some stats about the [current predictor](app/services/NextActionPredictor.scala#L35)) that is used for matching company profiles and company entities