package classification;

import algorithms.Classifier;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static settings.AppPropertyTypes.FINISHED_RUNNING_ALGORITHM;
import static settings.AppPropertyTypes.UNFINISHED_RUNNING_ALGORITHM;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;
    private TSDProcessor tsd;
    private LineChart<Number, Number> chart;
    private List<List<Integer>> outputs = new ArrayList<List<Integer>>();
    private ApplicationTemplate applicationTemplate;

    private Label unfinished = new Label();
    private Label finished = new Label();


    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

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

    public List<List<Integer>> getOutputs(){ return outputs; }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        new RandomClassifier(dataset,maxIterations,updateInterval,tocontinue, new TSDProcessor(), new LineChart<Number, Number>(new NumberAxis(), new NumberAxis()), new ApplicationTemplate());
    }
    public RandomClassifier(DataSet dataset, int maxIterations, int updateInterval, boolean tocontinue, TSDProcessor tsd, LineChart<Number, Number> chart, ApplicationTemplate applicationTemplate){
        this.dataset=dataset;
        this.maxIterations=maxIterations;
        this.updateInterval=updateInterval;
        this.tocontinue=new AtomicBoolean(tocontinue);
        this.tsd=tsd;
        this.chart= chart;
        this.applicationTemplate=applicationTemplate;

    }

    @Override
    public synchronized void run() {
//        if(((AppUI)applicationTemplate.getUIComponent()).getAlgState().getText().equals(
//                applicationTemplate.manager.getPropertyValue(FINISHED_RUNNING_ALGORITHM.name())))
//            ((AppUI)applicationTemplate.getUIComponent()).setAlgState("");
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


            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
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
                ((AppUI) applicationTemplate.getUIComponent()).setFirstRandomClassifier(false);
                Collections.sort(dataset.getxComponent());
                Double min = dataset.getxComponent().get(0);
                Double max = dataset.getxComponent().get(dataset.getxComponent().size() - 1);
                XYChart.Series series = tsd.equationSolver(min, max, output);
                if (i % updateInterval == 0 || i==maxIterations) {
                    Thread.sleep(1000);
                    Platform.runLater(() -> chart.getData().add(series));
                    Thread.sleep(1000);
                    if(!tocontinue()) {
                        ((AppUI) applicationTemplate.getUIComponent()).getScreenshotButton().setDisable(false);
                        ((AppUI) applicationTemplate.getUIComponent()).getDisplayButton().setDisable(false);
//                        ((AppUI) applicationTemplate.getUIComponent()).setAlgState(applicationTemplate.manager.getPropertyValue(UNFINISHED_RUNNING_ALGORITHM.name()));
//                    ((AppUI) applicationTemplate.getUIComponent()).getDisplayButtonBox().getChildren().add(unfinished);
                        wait();
                    }
                    if (i != maxIterations)
                        Platform.runLater(() -> chart.getData().remove(series));
                }

                } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        ((AppUI) applicationTemplate.getUIComponent()).setRunningState(false);
        ((AppUI)applicationTemplate.getUIComponent()).getScreenshotButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).getDisplayButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).getConfigButton1().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).getConfigButton2().setDisable(false);
//        ((AppUI)applicationTemplate.getUIComponent()).getDisplayButtonBox().getChildren().remove(unfinished);
//        finished.setText(applicationTemplate.manager.getPropertyValue(FINISHED_RUNNING_ALGORITHM.name()));
//        ((AppUI)applicationTemplate.getUIComponent()).setAlgState(applicationTemplate.manager.getPropertyValue(FINISHED_RUNNING_ALGORITHM.name()));
        ((AppUI)applicationTemplate.getUIComponent()).setFirstRandomClassifier(true);
    //    ((AppUI)applicationTemplate.getUIComponent()).getDisplayButtonBox().getChildren().add(finished);
    //    System.out.println("done");
        // if continue is false, each display button increases the i counter in the for loop until max iterations, then display button is disabled.
    }



    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    }
}