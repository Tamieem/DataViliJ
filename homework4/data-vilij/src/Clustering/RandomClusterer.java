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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer {

    private DataSet dataset;
    private List<Point2D> centroids;

    private List<List<Integer>> outputs = new ArrayList<List<Integer>>();
    private List<Integer> output;


    private static final Random RAND = new Random();

    private  int           maxIterations;
    private  int           updateInterval;
    private  AtomicBoolean tocontinue;
    private TSDProcessor tsd;
    private ApplicationTemplate applicationTemplate;
    private LineChart chart;

    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
        new RandomClusterer(dataset, maxIterations, updateInterval, false, numberOfClusters);
    }
    public RandomClusterer(DataSet dataSet, int maxIterations, int updateInterval, boolean tocontinue, int numberOfClusters){
        super(numberOfClusters);
        this.dataset=dataSet;
        this.maxIterations= maxIterations;
        this.updateInterval= updateInterval;
        this.tocontinue= new AtomicBoolean(tocontinue);
        new RandomClusterer(dataset, maxIterations, updateInterval, tocontinue, numberOfClusters, new TSDProcessor(),
                new LineChart<Number, Number>(new NumberAxis(), new NumberAxis()), new ApplicationTemplate());

    }
    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, boolean tocontinue, int numberOfClusters, TSDProcessor tsd, LineChart<Number, Number> chart, ApplicationTemplate applicationTemplate){
        super(numberOfClusters);
        this.dataset=dataset;
        this.maxIterations=maxIterations;
        this.updateInterval=updateInterval;
        this.tocontinue=new AtomicBoolean(tocontinue);
        this.tsd=tsd;
        this.chart= chart;
        this.applicationTemplate=applicationTemplate;

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
    public synchronized void run() {
        for (int i = 1; i <= maxIterations; i++) {
            ((AppUI) applicationTemplate.getUIComponent()).setRunningState(true);
            ((AppUI) applicationTemplate.getUIComponent()).getScreenshotButton().setDisable(true);
            ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setDisable(true);

            int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
            int yCoefficient = 10;
            int constant     = RAND.nextInt(11);
            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);
            outputs.add(output);

            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i); //
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                flush();
                break;
            }

            try {
                ((AppUI) applicationTemplate.getUIComponent()).setFirstRun(false);
                Collections.sort(dataset.getxComponent());
                Double min = dataset.getxComponent().get(0);
                Double max = dataset.getxComponent().get(dataset.getxComponent().size() - 1);
                XYChart.Series series = tsd.equationSolver(min, max, output);
                series.getNode().setVisible(false);
                if (i % updateInterval == 0 || i==maxIterations) {
                    Platform.runLater(() -> chart.getData().add(series));
                    Thread.sleep(1000);
                    if(!tocontinue()) {
                        ((AppUI) applicationTemplate.getUIComponent()).getScreenshotButton().setDisable(false);
                        ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setDisable(false);
//                        ((AppUI) applicationTemplate.getUIComponent()).setAlgState(applicationTemplate.manager.getPropertyValue(UNFINISHED_RUNNING_ALGORITHM.name()));
//                    ((AppUI) applicationTemplate.getUIComponent()).getDisplayButtonBox().getChildren().add(unfinished);
                        wait();
                    }
                    if (i != maxIterations) {
                        Platform.runLater(() -> chart.getData().remove(series));
                        Thread.sleep(1000);
                    }
                }

            } catch (InterruptedException e) {
                System.out.print("");
            }

        }
        ((AppUI) applicationTemplate.getUIComponent()).setRunningState(false);
        ((AppUI)applicationTemplate.getUIComponent()).getScreenshotButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).getDisplayButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).setClassificationConfigButtons(
                false);
        ((AppUI)applicationTemplate.getUIComponent()).setFirstRun(true);
  }



    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClusterer clusterer = new RandomClusterer(dataset, 100, 5, true, 3);
        clusterer.run(); // no multithreading yet
    }
}