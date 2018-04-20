package Strategies;

import classification.DataSet;

public class ClassificationContext {
    private ClassificationStrategy strategy;
    public void setClassificationStrategy(ClassificationStrategy strategy){
        this.strategy=strategy;
    }
    public void runAlgorithm(DataSet dataSet){
        strategy.displayClassification(dataSet);
    }
}
