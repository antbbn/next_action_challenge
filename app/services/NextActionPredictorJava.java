package services;

import model.Interaction;

import java.util.Random;

public class NextActionPredictorJava implements NextActionPredictor {
    private Random r = new Random(42);

    @Override
    public int predictNextAction(int userId, long timestamp) {
        return r.nextInt(4) + 1;
    }

    @Override
    public boolean storeFeedback(Interaction i) {
        return true;
    }
}
