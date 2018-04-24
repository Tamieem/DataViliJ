package algorithms;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import vilij.templates.ApplicationTemplate;

import java.util.Arrays;
import java.util.List;

import static settings.AppPropertyTypes.*;

public class AlgorithmConfiguration extends Stage{

    public enum RunConfig {

        OK("Ok"), CANCEL("Cancel");

        @SuppressWarnings("unused")
        private String option;

        RunConfig(String option) { this.option = option; }
    }
    ApplicationTemplate applicationTemplate;

    private int maxIterations;
    private int updateInterval;
    private int numberOfClusters = 0;
    private boolean toContinue;
    private TextArea upInt = new TextArea();
    private TextArea maxIt = new TextArea();
    private CheckBox contRun = new CheckBox();
    private TextArea numClusters = new TextArea();


    private RunConfig selectedOption;
    private String configuration;

    public AlgorithmConfiguration(ApplicationTemplate applicationTemplate, String configuration ) {
        this.configuration = configuration;
        this.applicationTemplate=applicationTemplate;
    }

    public int getMaxIterations() {
        return maxIterations;
    }
    public void setMaxIterations(int maxIterations){
        this.maxIterations=maxIterations;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }
    public void setUpdateInterval(int updateInterval){
        this.updateInterval=updateInterval;
    }

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

        final Stage dialog = new Stage();



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

        if(maxIt.getText().matches("[a-z]"))
            maxIt.setText("0");
        if(numClusters.getText().matches("[a-z]"))
            numClusters.setText("0");
        if(upInt.getText().matches("[a-z]"))
            upInt.setText("0");
        Label maxIteration = new Label(applicationTemplate.manager.getPropertyValue(MAX_ITERATION.name()) + "\t");
        maxIt.setMaxSize(10,10);
        maxIterations.getChildren().addAll(maxIteration, maxIt);

        Label UpdateInterval = new Label(applicationTemplate.manager.getPropertyValue(UPDATE_INTERVAL.name()) + "\t");
        upInt.setMaxSize(10,10);
        updateInterval.getChildren().addAll(UpdateInterval, upInt);

        Label ContinuousRun = new Label(applicationTemplate.manager.getPropertyValue(CONTINUOUS_RUN.name())+ "\t");
        continuousRun.getChildren().addAll(ContinuousRun, contRun);

        Label NumberOfClusters = new Label(applicationTemplate.manager.getPropertyValue(NUMBER_OF_CLUSTERS.name())+ " ");
        numClusters.setMaxSize(10,10);
        numOfClusters.getChildren().addAll(NumberOfClusters, numClusters);

        VBox config = new VBox();
        config.getChildren().addAll(maxIterations, updateInterval, continuousRun);
        if(configuration.equals(applicationTemplate.manager.getPropertyValue(CLUSTERING.name())))
            config.getChildren().addAll(numOfClusters);

        config.setAlignment(Pos.CENTER);
        config.setPadding(new Insets(10, 20, 20, 20));
        config.setSpacing(10);

        config.getChildren().add(buttonBox);

        this.setScene(new Scene(config));
        this.showAndWait();
    }

    public void run(Stage owner) {
        setWorkspaceActions();
        init(owner);

    }

    private void setWorkspaceActions(){
        if(maxIt.getText().matches(".*[a-zA-Z]+.*"))
            maxIt.setText("0");
        if(numClusters.getText().matches(".*[a-zA-Z]+.*"))
            numClusters.setText("0");
        if(upInt.getText().matches(".*[a-zA-Z]+.*"))
            upInt.setText("0");
        maxIt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    maxIterations = Integer.parseInt(maxIt.getText());
                } catch(NumberFormatException e){
                    maxIterations=0;
                }
            }
        });
        upInt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    updateInterval = Integer.parseInt(upInt.getText());
                }catch (NumberFormatException e){
                    updateInterval= 1;
                }
            }
        });
        numClusters.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    numberOfClusters = Integer.parseInt(numClusters.getText());
                } catch(NumberFormatException e){
                    numberOfClusters=0;
                }
            }
        });

        contRun.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                if(contRun.isSelected()){
                    toContinue=true;
                }
                else if(!contRun.isSelected())
                    toContinue=false;
            }
        });

    }
}
