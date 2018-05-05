package Clustering;
import algorithms.Clusterer;
import classification.DataSet;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends Clusterer {

    private DataSet dataset;
    private List<Point2D> centroids;
    private TSDProcessor tsd;
    private LineChart chart;
    private ApplicationTemplate applicationTemplate;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    private boolean continuous;
    XYChart.Series<Number, Number> series = new XYChart.Series<>();


    public KMeansClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
        new KMeansClusterer(dataset, maxIterations, updateInterval, false, numberOfClusters);
    }
    public KMeansClusterer(DataSet dataSet, int maxIterations, int updateInterval, boolean tocontinue, int numberOfClusters){
        super(numberOfClusters);
        this.dataset=dataSet;
        this.maxIterations= maxIterations;
        this.updateInterval= updateInterval;
        this.tocontinue= new AtomicBoolean(tocontinue);
        continuous=tocontinue;
        new KMeansClusterer(dataset,maxIterations,updateInterval,tocontinue, numberOfClusters, new TSDProcessor(), new LineChart<Number, Number>(new NumberAxis(), new NumberAxis()), new ApplicationTemplate());
    }

    public KMeansClusterer(DataSet dataset, int maxIterations, int updateInterval, boolean tocontinue, int numberOfClusters, TSDProcessor tsdProcessor, LineChart<Number, Number> numberNumberLineChart,
                           ApplicationTemplate applicationTemplate) {
        super(numberOfClusters);
        this.dataset=dataset;
        this.maxIterations= maxIterations;
        this.updateInterval= updateInterval;
        this.tocontinue= new AtomicBoolean(tocontinue);
        continuous=tocontinue;
        tsd= tsdProcessor;
        chart= numberNumberLineChart;
        this.applicationTemplate= applicationTemplate;
    }


    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return tocontinue.get(); }

    @Override
    public synchronized void run() {
        int iteration = 0;
        while (iteration <= maxIterations) {
            ((AppUI) applicationTemplate.getUIComponent()).setRunningState(true);
            ((AppUI) applicationTemplate.getUIComponent()).getScreenshotButton().setDisable(true);
            ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setDisable(true);
            initializeCentroids();
            assignLabels();
            try {
                ((AppUI) applicationTemplate.getUIComponent()).setFirstRun(false);
                tsd = new TSDProcessor(dataset.getLocations(), dataset.getLabels());
                if (iteration % updateInterval == 0 || iteration == maxIterations) {
                    Platform.runLater(() -> tsd.toChartData(chart));
                    Thread.sleep(1000);
                    if (!continuous) {
                        ((AppUI) applicationTemplate.getUIComponent()).getScreenshotButton().setDisable(false);
                        ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setDisable(false);
                        wait();
                    }
//                        series.getNode().setVisible(false);
//                        series.getNode().setStyle("-fx-stroke: transparent");
                    if(iteration != maxIterations) {
                        Platform.runLater(() -> chart.getData().clear());
                        Thread.sleep(1000);
                    }
                    //   }
                }
                iteration++;
            }catch ( InterruptedException | ArrayIndexOutOfBoundsException e) {
                System.out.print("");
            }
            recomputeCentroids();
        }
        ((AppUI) applicationTemplate.getUIComponent()).setRunningState(false);
        ((AppUI)applicationTemplate.getUIComponent()).getScreenshotButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).getDisplayButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).setClusteringConfigButtons(false);
        ((AppUI) applicationTemplate.getUIComponent()).setFirstRun(true);
    }

    private void initializeCentroids() {
        Set<String> chosen = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataset.getLabels().keySet());
        Random r = new Random();
        try {
            while (chosen.size() < numberOfClusters) {
                int i = r.nextInt(instanceNames.size());
                while (chosen.contains(instanceNames.get(i)))
                    ++i;
                chosen.add(instanceNames.get(i));
            }
            centroids = chosen.stream().map(name -> dataset.getLocations().get(name)).collect(Collectors.toList());
            tocontinue.set(true);
        } catch(IndexOutOfBoundsException e){
            System.out.print("");
        }
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataset.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {
        tocontinue.set(false);
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataset.getLabels()
                    .entrySet()
                    .stream()
                    .filter(entry -> i == Integer.parseInt(entry.getValue()))
                    .map(entry -> dataset.getLocations().get(entry.getKey()))
                    .reduce(new Point2D(0, 0), (p, q) -> {
                        clusterSize.incrementAndGet();
                        return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                    });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                tocontinue.set(true);
            }
        });
    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

}
