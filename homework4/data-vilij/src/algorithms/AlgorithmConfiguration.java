package algorithms;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class AlgorithmConfiguration extends Stage implements Algorithm{

    public enum RunConfig {

        OK("Ok"), CANCEL("Cancel");

        @SuppressWarnings("unused")
        private String option;

        RunConfig(String option) { this.option = option; }
    }

    private int maxIterations;
    private int updateInterval;
    private int numberOfClusters;
    private boolean toContinue;
    private TextArea upInt;
    private TextArea maxIt;
    private CheckBox contRun;
    private TextArea numClusters;


    private RunConfig selectedOption;
    private String configuration;

    public AlgorithmConfiguration(String configuration ) {
        this.configuration = configuration;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }
    public void setMaxIterations(int maxIterations){
        this.maxIterations=maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }
    public void setUpdateInterval(int updateInterval){
        this.updateInterval=updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return toContinue;
    }
    public void setToContinue(boolean toContinue){
        this.toContinue=toContinue;
    }

    public int getNumberOfClusters(){
        return numberOfClusters;
    }
    public void setNumberOfClusters(int numberOfClusters) {
        this.numberOfClusters=numberOfClusters;
    }

    public TextArea getMaxIt() { return maxIt; }

    public TextArea getUpInt() { return upInt; }

    public CheckBox getContRun() { return contRun; }

    public RunConfig getSelectedOption() { return selectedOption; }

    public TextArea getNumClusters() { return numClusters; }

    public void init(Stage owner) {
        initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        initOwner(owner);


        List<Button> buttons = Arrays.asList(new Button(RunConfig.OK.name()),
                new Button(RunConfig.CANCEL.name()));

        buttons.forEach(button -> button.setOnAction((ActionEvent event) -> {
            this.selectedOption = RunConfig.valueOf(((Button) event.getSource()).getText());
            this.hide();
        }));

        HBox buttonBox = new HBox(5);
        buttonBox.getChildren().addAll(buttons);


        HBox maxIterations = new HBox(5);
        HBox updateInterval = new HBox(5);
        HBox continuousRun = new HBox(5);
        HBox numOfClusters = new HBox(5);

        Label maxIteration = new Label("Max. Iterations: " + "\t");
        maxIt = new TextArea();
        maxIt.setMinWidth(50);
        maxIt.setPrefWidth(50);
        maxIt.setMaxWidth(400);
        maxIterations.getChildren().addAll(maxIteration, maxIt);

        Label UpdateInterval = new Label(" Update Interval: " + "\t");
        upInt = new TextArea();
        upInt.setMinWidth(50);
        upInt.setPrefWidth(50);
        upInt.setMaxWidth(400);
        updateInterval.getChildren().addAll(UpdateInterval, upInt);

        Label ContinuousRun = new Label("Continuous Run? " + "\t");
        contRun = new CheckBox();
        continuousRun.getChildren().addAll(ContinuousRun, contRun);

        Label NumberOfClusters = new Label("Number of Clusters: " + "\t");
        numClusters = new TextArea();
        numClusters = new TextArea();
        numClusters.setMinWidth(50);
        numClusters.setPrefWidth(50);
        numClusters.setMaxWidth(400);
        numOfClusters.getChildren().addAll(NumberOfClusters, numClusters);

        VBox config = new VBox();
        config.getChildren().addAll(maxIterations, updateInterval, continuousRun);
        if(configuration.equals("Clustering"))
            config.getChildren().addAll(numOfClusters);

        config.setAlignment(Pos.CENTER);
        config.setPadding(new Insets(10, 20, 20, 20));
        config.setSpacing(10);

        config.getChildren().add(buttonBox);

        this.setScene(new Scene(config));
    }

    @Override
    public void run() {
        showAndWait();
    }
}
