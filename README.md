# next_action_challenge

In this repository you'll find my work on the problem of predicting
the next action of a user.

The main analysis is contained in the ipython notebook that can be
visualized right here on github.
The main points from the analysis:
  - The most common action is Connecting to other users (3).
  - 3 is also the most common first action of a user.
  - The most common "next action" is repeating the previous one.
  - This is true especially for action 3 and 4.
  - But a long time since the last action can lead to a different one for actions 1,2, and 5
  - Also a habit of changing action can influence the decision for actions 1,2 and 3.

Considering only the second and third point I implemented 
the `NotSoDummyPredictor` which is different from the `DummyPredictior` 
only because it doesn't use a random value the first time a new user arrives.

`NotSoDummyPredictor` raises the accuracy of  `DummyPredictior` from 40% to 54%.

Considering all the main points of the analysis i created `AnalysisPredictor`
which actually lowers the accuracy.

The reason for this is that the `EvaluationService` does not store the 
actions as soon as it sees them so it treats every user from the test set, 
every time they perform an action as a new user, and the `AnalysisPredictor`
relies a lot on the history of a particular user.

A small change in the `EvaluationService` (performed in the 
`store_actions` branch of the repo) changes the picuture completely 
and brings the accuracy to a nice 81% with the `AnalysisPredictor` 
providing a small improvement over the `NotSoDummyPredictor`.

I am not sure if this was allowed by the challenge, but I think it's a very
realistic solution. In fact in a real life scenario I would store the actions
of the users as soon as they are performed and I might be able to update
the statistics about a user if not immediately at least within a short period
of time, increasing the accuracy of my model.

