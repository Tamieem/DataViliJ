package Clustering;
import algorithms.Clusterer;
import classification.DataSet;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer {

    private DataSet dataset;
    private List<Point2D> centroids;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;

    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
        new KMeansClusterer(dataset, maxIterations, updateInterval, false, numberOfClusters);
    }
    public RandomClusterer(DataSet dataSet, int maxIterations, int updateInterval, boolean tocontinue, int numberOfClusters){
        super(numberOfClusters);
        this.dataset=dataSet;
        this.maxIterations= maxIterations;
        this.updateInterval= updateInterval;
        this.tocontinue= new AtomicBoolean(tocontinue);

    }
    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    @Override
    public void run() {

    }
}
