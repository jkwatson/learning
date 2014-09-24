package com.flightstats.analytics.tree.multiclass;

import com.flightstats.analytics.tree.Item;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.flightstats.analytics.tree.multiclass.RandomForestTrainerTest.Humidity.HIGH;
import static com.flightstats.analytics.tree.multiclass.RandomForestTrainerTest.Humidity.NORMAL;
import static com.flightstats.analytics.tree.multiclass.RandomForestTrainerTest.Outlook.*;
import static com.flightstats.analytics.tree.multiclass.RandomForestTrainerTest.Temp.*;
import static com.flightstats.analytics.tree.multiclass.RandomForestTrainerTest.Wind.STRONG;
import static com.flightstats.analytics.tree.multiclass.RandomForestTrainerTest.Wind.WEAK;
import static org.junit.Assert.assertEquals;

public class RandomForestTrainerTest {

    @Test
    public void testTennisExample() throws Exception {
        List<LabeledItem> trainingData = buildTennisTrainingSet();

        RandomForestTrainer testClass = new RandomForestTrainer(new DecisionTreeTrainer(new EntropyCalculator()));
        List<String> attributes = tennisAttributes();

        TrainingResults result = testClass.train("tennis", 5, trainingData, attributes, -1);
        RandomForest tennis = result.getForest();
        //this is a nice case of where the random forest works probabilistically. It's not a clear-cut case,
        // so not all the trees return the same result. On average, though, these should be a good day for tennis.
        assertEquals((Integer) 1, tennis.evaluate(new Item("1", tennisData(RAIN, HOT, NORMAL, WEAK))));
        assertEquals((Integer) 1, tennis.evaluate(new Item("1", tennisData(RAIN, MILD, HIGH, WEAK))));
    }

    @Test
    public void testGsonSerialization() throws Exception {
        RandomForest randomForest = new RandomForestTrainer(new DecisionTreeTrainer(new EntropyCalculator())).train("test", 50, buildTennisTrainingSet(), tennisAttributes(), 0).getForest();
        Gson gson = new Gson();
        String json = gson.toJson(randomForest);

        RandomForest deserialized = gson.fromJson(json, RandomForest.class);
        assertEquals(randomForest, deserialized);
    }

    public static List<String> tennisAttributes() {
        return Arrays.asList("outlook", "temp", "humidity", "wind");
    }


    public static List<LabeledItem> buildTennisTrainingSet() {
        return Arrays.asList(
                new LabeledItem(new Item("1", tennisData(SUNNY, HOT, HIGH, WEAK)), 0),
                new LabeledItem(new Item("2", tennisData(SUNNY, HOT, HIGH, STRONG)), 0),
                new LabeledItem(new Item("3", tennisData(OVERCAST, HOT, HIGH, WEAK)), 1),
                new LabeledItem(new Item("4", tennisData(RAIN, MILD, HIGH, WEAK)), 1),

                new LabeledItem(new Item("5", tennisData(RAIN, COOL, NORMAL, WEAK)), 1),
                new LabeledItem(new Item("6", tennisData(RAIN, COOL, NORMAL, STRONG)), 0),
                new LabeledItem(new Item("7", tennisData(OVERCAST, COOL, NORMAL, STRONG)), 1),
                new LabeledItem(new Item("8", tennisData(SUNNY, MILD, HIGH, WEAK)), 0),

                new LabeledItem(new Item("9", tennisData(SUNNY, COOL, NORMAL, WEAK)), 1),
                new LabeledItem(new Item("10", tennisData(RAIN, MILD, NORMAL, WEAK)), 1),
                new LabeledItem(new Item("11", tennisData(SUNNY, MILD, NORMAL, STRONG)), 1),
                new LabeledItem(new Item("12", tennisData(OVERCAST, MILD, HIGH, STRONG)), 1),

                new LabeledItem(new Item("13", tennisData(OVERCAST, HOT, NORMAL, WEAK)), 1),
                new LabeledItem(new Item("14", tennisData(RAIN, MILD, HIGH, STRONG)), 0)
        );
    }

    public static Map<String, Integer> tennisData(Outlook outlook, Temp temperature, Humidity humidity, Wind wind) {
        Map<String, Integer> data = new HashMap<>();
        data.put("outlook", outlook.value);
        data.put("temp", temperature.value);
        data.put("humidity", humidity.value);
        data.put("wind", wind.value);
        return data;
    }

    @AllArgsConstructor
    static enum Outlook {
        SUNNY(1), OVERCAST(2), RAIN(3);
        int value;
    }

    @AllArgsConstructor
    static enum Temp {
        HOT(1), MILD(2), COOL(3);
        int value;
    }

    @AllArgsConstructor
    static enum Humidity {
        HIGH(1), NORMAL(2);
        int value;
    }

    @AllArgsConstructor
    static enum Wind {
        WEAK(1), STRONG(2);
        int value;
    }

}