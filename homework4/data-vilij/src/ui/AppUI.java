package ui;

import Clustering.KMeansClusterer;
import Clustering.RandomClusterer;
import Strategies.ClassificationContext;
import Strategies.RandomClassificationStrategy;
import actions.AppActions;
import algorithms.AlgorithmConfiguration;
import algorithms.Classifier;
import algorithms.Clusterer;
import classification.DataSet;
import classification.RandomClassifier;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;
import vilij.components.Dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static settings.AppPropertyTypes.*;
import static settings.AppPropertyTypes.CSS_RESOURCE_FILENAME;
import static settings.AppPropertyTypes.CSS_RESOURCE_PATH;
import static settings.AppPropertyTypes.GUI_RESOURCE_PATH;
import static settings.AppPropertyTypes.ICONS_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.*;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private static final String SEPARATOR= "/";

    private Button scrnshotButton; // toolbar button to take a screenshot of the data
    private String                       scrnshotPath; // path to screenshot button
    private String                       cssPath;
    private String                       lineChartPath;
    private String                       textAreaPath;
    private ScatterChart<Number, Number> chart1;          // the chart where data will be displayed
    private LineChart<Number, Number>    chart;
    private Button                       displayButton;// workspace button to display data on the chart
    private Button                       validateButton;
    private TextArea textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                       data;
    private Label label;
    private CheckBox cb1;
    private List<Button> classifcationConfigButtons= new ArrayList<>();
    private List<Button> clusteringConfigButtons = new ArrayList<>();

    private ChoiceBox cb = new ChoiceBox();
    private VBox vB = new VBox();
    private HBox hB= new HBox();
    // vB Holds Text Area and checkboxes and text information.

    private Button ClassificationConfigButton;
    private Button KMeansClusteringConfig;
    private Button RandomClusteringConfig;
    private Label algState = new Label("");

    private List options;
    private ToggleGroup clusteringGroup = new ToggleGroup();
    private HBox RandomclusteringHB = new HBox();
    private HBox KMeansClusteringHB = new HBox();
    private RadioButton clustrb1 = new RadioButton("KMeans Clustering ");
    private RadioButton clustrb2= new RadioButton("Random Clustering ");
    private VBox ClusteringChoices = new VBox();
    private VBox ClassificationChoices = new VBox();
    private HBox displayButtonBox = new HBox();


    private ToggleGroup classificationGroup = new ToggleGroup();
    private HBox classifcationHB = new HBox();
    private RadioButton classrb1 = new RadioButton("Random Classifcation");

    private boolean savedConfig=false;
    private String configuration;
    private AlgorithmConfiguration runConfig;

    private Classifier classifier;
    private TSDProcessor tsd;
    private Thread runAlg;

    private DataSet dataSet = new DataSet();
    private ClassificationContext classificationContext = new ClassificationContext();

    public void setLabel(Label label) { this.label = label; }

    public HBox getHB(){ return hB; }
    public VBox getVB(){ return vB; }
    public TextArea getTextArea(){ return textArea; }
    public Label getLabel(){ return label; }
    public ChoiceBox getChoiceBox(){ return cb; }
    public boolean getHasNewText(){ return this.hasNewText; }


    public List getOptions(){ return options; }
    public void setOptions(List options){ this.options = options; }


    public LineChart<Number, Number> getLineChart(){ return chart; }


    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                applicationTemplate.manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnshotPath =String.join(SEPARATOR, iconsPath,
                applicationTemplate.manager.getPropertyValue(SCREENSHOT_ICON.name()));
        cssPath= SEPARATOR + String.join(SEPARATOR,
                applicationTemplate.manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(CSS_RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(CSS_RESOURCE_FILENAME.name()));
        lineChartPath= SEPARATOR + String.join(SEPARATOR,
                applicationTemplate.manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(CSS_RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(LINECHART_FILENAME.name()));
        textAreaPath= SEPARATOR + String.join(SEPARATOR,
                applicationTemplate.manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(CSS_RESOURCE_PATH.name()),
                applicationTemplate.manager.getPropertyValue(TEXTAREA_FILENAME.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1

        PropertyManager manager = applicationTemplate.manager;
        newButton = setToolbarButton(newiconPath, manager.getPropertyValue(NEW_TOOLTIP.name()), false);
        saveButton = setToolbarButton(saveiconPath, manager.getPropertyValue(SAVE_TOOLTIP.name()), true);
        loadButton = setToolbarButton(loadiconPath, manager.getPropertyValue(LOAD_TOOLTIP.name()), false);
        printButton = setToolbarButton(printiconPath, manager.getPropertyValue(PRINT_TOOLTIP.name()), true);
        exitButton = setToolbarButton(exiticonPath, manager.getPropertyValue(EXIT_TOOLTIP.name()), false);
        scrnshotButton= setToolbarButton(scrnshotPath,manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar = new ToolBar(newButton, loadButton, saveButton, scrnshotButton, exitButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        AppActions action= new AppActions(applicationTemplate);
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> {
            try {
                action.handleScreenshotRequest();
            } catch (IOException e1) {
                ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(ERROR_DATA.name()));
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
        chart.getStylesheets().addAll(getClass().getResource(cssPath).toExternalForm());
    }

    @Override
    public void clear() {
        // TODO for homework 1
        textArea.clear();
        chart.getData().clear();
        scrnshotButton.setDisable(true);
        newButton.setDisable(true);
        saveButton.setDisable(true);
        hasNewText=false;

    }

    private void layout() {
        // TODO for homework 1
        chart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
        chart.setTitle(applicationTemplate.manager.getPropertyValue(CHART_TITLE.name()));
        StackPane sp = new StackPane(chart);
        sp.setMaxSize(windowWidth * 0.59, windowHeight * 0.59);
        sp.setMinSize(windowWidth * 0.59, windowHeight * 0.59);
        StackPane.setAlignment(sp, Pos.CENTER);
        BorderPane bp = new BorderPane();
        appPane.getChildren().add(bp);
        bp.setRight(sp);
        textArea = new TextArea();
        displayButton = new Button(applicationTemplate.manager.getPropertyValue(DISPLAY.name()));
        vB.setMaxWidth(windowWidth*.41);
        bp.setLeft(vB);

        cb1= new CheckBox(applicationTemplate.manager.getPropertyValue(READ_ONLY.name()));
        validateButton = new Button(applicationTemplate.manager.getPropertyValue(VALIDATE.name()));
        validateButton.setDisable(true);
        validateButton.setOnAction(e -> ((AppActions) applicationTemplate.getActionComponent()).handleValidationRequest(textArea.getText()));
        hB.getChildren().addAll(cb1, validateButton);
        ClusteringList();
        ClassificationList();
//
//        ClassificationConfigButton = new Button(applicationTemplate.manager.getPropertyValue(CONFIGURE.name()));
//        ClassificationConfigButton.setOnAction(event -> handleRunConfiguration());
//
//        RandomClusteringConfig = new Button(applicationTemplate.manager.getPropertyValue(CONFIGURE.name()));
//        RandomClusteringConfig.setOnAction(event -> handleRunConfiguration());
//
//        KMeansClusteringConfig = new Button(applicationTemplate.manager.getPropertyValue(CONFIGURE.name()));
//        KMeansClusteringConfig.setOnAction(event -> handleRunConfiguration());
//        classifcationHB.getChildren().addAll(classrb1, ClassificationConfigButton);
//
//        classrb1.setToggleGroup(classificationGroup);
//        classrb1.setSelected(true);
        displayButton.setDisable(true);
//        ClassificationChoices.getChildren().addAll(classrb1, ClassificationConfigButton);
//        KMeansClusteringHB.getChildren().addAll(clustrb1, KMeansClusteringConfig);
//        RandomclusteringHB.getChildren().addAll(clustrb2, RandomClusteringConfig);
//        clustrb1.setToggleGroup(clusteringGroup);
//        clustrb2.setToggleGroup(clusteringGroup);
//        clustrb2.setSelected(false);
//        ClusteringChoices.getChildren().addAll(RandomclusteringHB, KMeansClusteringHB);
        displayButtonBox.getChildren().addAll(displayButton, algState);
    }
    private void ClassificationList(){

        String filePath = new File("").getAbsolutePath();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("ClassificationMethods.txt")))) {
            String line;
            while ((line = reader.readLine()) != null){
                RadioButton classRB = new RadioButton(line);
                classRB.setSelected(false);
                classRB.setToggleGroup(classificationGroup);
                Button classConfig = new Button(applicationTemplate.manager.getPropertyValue(CONFIGURE.name()));
                classifcationConfigButtons.add(classConfig);
                classConfig.setOnAction(event -> handleRunConfiguration());
                HBox classHBox = new HBox();
                classHBox.getChildren().addAll(classRB, classConfig);
                ClassificationChoices.getChildren().add(classHBox);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void ClusteringList(){
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("ClusteringMethods.txt")))) {
            String line;
            while ((line = reader.readLine()) != null){
                RadioButton clustRB = new RadioButton(line);
                clustRB.setSelected(false);
                clustRB.setToggleGroup(clusteringGroup);
                Button clustConfig = new Button(applicationTemplate.manager.getPropertyValue(CONFIGURE.name()));
                clusteringConfigButtons.add(clustConfig);
                clustConfig.setOnAction(event -> handleRunConfiguration());
                HBox clustHBox = new HBox();
                clustHBox.getChildren().addAll(clustRB, clustConfig);
                ClusteringChoices.getChildren().add(clustHBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String selectedClassification;
    private String selectedCluster;

    public void setWorkspaceActions(){
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newButton.setDisable(false);
                saveButton.setDisable(false);
                hasNewText= true;
                String data= textArea.getText();
                if(data.isEmpty()){
                    newButton.setDisable(true);
                    saveButton.setDisable(true);
                    hasNewText= false;
                }
            }
        });// Makes New and Save button clickable
        cb1.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                if(cb1.isSelected()){
                    textArea.setDisable(true);
                    validateButton.setDisable(false);
                }
                else if(!cb1.isSelected()) {
                    textArea.setDisable(false);
                    validateButton.setDisable(true);
                };
            }
        });
        chart.setCursor(Cursor.CROSSHAIR);
        textArea.setPrefRowCount(10);

        classificationGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {

                RadioButton chk = (RadioButton)new_toggle.getToggleGroup().getSelectedToggle(); // Cast object to radio button
                selectedClassification = chk.getText();

            }
        });
        clusteringGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {

                RadioButton chk = (RadioButton)new_toggle.getToggleGroup().getSelectedToggle(); // Cast object to radio button
                selectedCluster = chk.getText();

            }
        });





        cb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                try {
                    if (newValue.intValue() == -1) ;

                    if (((String) options.get(oldValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(CLASSIFICATION.name())))
                        vB.getChildren().removeAll(ClassificationChoices, displayButtonBox );
                    else if (((String) options.get(oldValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(CLUSTERING.name())))
                        vB.getChildren().removeAll(ClusteringChoices, displayButtonBox);

                    if (((String) options.get(newValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(CLASSIFICATION.name()))) {
                        if (((String) options.get(oldValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(CLUSTERING.name()))) {
                            runConfig = new AlgorithmConfiguration(applicationTemplate, (String) options.get(newValue.intValue()));
                            displayButton.setDisable(true);
                            savedConfig = false;
                        } else if (((String) options.get(oldValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(SELECT_ALGORITHM_TYPE.name()))) {
                            runConfig = new AlgorithmConfiguration(applicationTemplate, (String) options.get(newValue.intValue()));
                            displayButton.setDisable(true);
                        }
                        vB.getChildren().add(ClassificationChoices);
                        vB.getChildren().add(displayButtonBox);
                        displayButton.setOnAction(e -> handleClassificationDisplayRequest());

                    } else if (((String) options.get(newValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(CLUSTERING.name()))) {
                        if (((String) options.get(oldValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(CLASSIFICATION.name()))) {
                           runConfig = new AlgorithmConfiguration(applicationTemplate, (String) options.get(newValue.intValue()));
                           displayButton.setDisable(true);
                           savedConfig = false;
                        } else if (((String) options.get(oldValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(SELECT_ALGORITHM_TYPE.name())))
                            runConfig = new AlgorithmConfiguration(applicationTemplate, (String) options.get(newValue.intValue()));
                        vB.getChildren().add(ClusteringChoices);
                        vB.getChildren().add(displayButtonBox);
                        displayButton.setOnAction(e -> handleClusteringDisplayRequest());
                    }
                    else if (((String) options.get(newValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(SELECT_ALGORITHM_TYPE.name()))) {
                        if (((String) options.get(oldValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(CLASSIFICATION.name()))) {
                            vB.getChildren().remove(ClassificationChoices);
                            savedConfig = false;
                        } else if (((String) options.get(oldValue.intValue())).equals(applicationTemplate.manager.getPropertyValue(CLUSTERING.name()))) {
                            runConfig = new AlgorithmConfiguration(applicationTemplate, (String) options.get(newValue.intValue()));
                            displayButton.setDisable(true);
                            vB.getChildren().remove(ClusteringChoices);
                        }
                        vB.getChildren().remove(displayButtonBox);
                    }
                } catch(IndexOutOfBoundsException e){}
            }
        });

    }

    public void setTextArea(String text){ textArea.setText(text); }

    public void handleDisplayRequest() { // CUSTOM METHOD
        AppData appData=new AppData(applicationTemplate);
        tsd = new TSDProcessor();
        chart.getData().clear();
        data= textArea.getText();
        try {
            tsd.processString(data);
            tsd.toChartData(chart);
            scrnshotButton.setDisable(false);
        } catch (Exception E) {
            ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(ERROR_DATA.name()));
        }

    }

    public void handleRunConfiguration() {
        runConfig.run(getPrimaryWindow());
        if (runConfig.getSelectedOption() == AlgorithmConfiguration.RunConfig.OK) {
            if(runConfig.getMaxIterations()>0 && runConfig.getNumberOfClusters()>-1 && runConfig.getUpdateInterval()>-1) {
                savedConfig = true;
                displayButton.setDisable(false);
            }
            else{
                savedConfig=false;
                displayButton.setDisable(true);
            }
        }
    }
    // ALL EXTRA METHODS NEEDED FOR DISPLAYING RANDOM CLASSIFICATION
    public void setAlgState(String string){ algState.setText(string); }
    public Label getAlgState(){ return algState; }
    public Button getClassificationConfigButton(){ return ClassificationConfigButton; }
    public Button getRandomClusteringConfigButton(){ return RandomClusteringConfig; }
    public Button getKMeansClusteringConfigButton(){ return KMeansClusteringConfig; }
    private boolean firstRun = true;
    public void setFirstRun(boolean val){ firstRun =val; }
    private boolean running = false;
    public void setRunningState(boolean val){ running = val;}
    public boolean getRunningState(){ return running; }
    public Button getDisplayButton(){ return displayButton; }
    public Button getScreenshotButton(){ return scrnshotButton; }
    public HBox getDisplayButtonBox() {return displayButtonBox; }
    public Classifier getClassifier(){ return classifier; }
    public Thread getRunAlg(){ return runAlg; }
    // SOME EXTRA METHODS ALSO BUT ITS OKAY TO HAVE THEM THERE


    public void handleClassificationDisplayRequest() {
        handleDisplayRequest();
        // TODO: hw5
        scrnshotButton.setDisable(true);
        displayButton.setDisable(true);
        for(Button button: classifcationConfigButtons){
            button.setDisable(true);
        }
        int maxIt = runConfig.getMaxIterations();
        int interval = runConfig.getUpdateInterval();
        boolean continuous = runConfig.tocontinue();
        //LineChart and shit
        if (continuous || firstRun) {
            if (((AppActions) applicationTemplate.getActionComponent()).getSavedFile() != null) {
                dataSet = new DataSet();
                try {
                    dataSet = dataSet.fromTSDFile(((AppActions) applicationTemplate.getActionComponent()).getSavedFile().toPath());
                } catch (IOException e) {
                    System.out.print("");
                }
            } else {
                try {
                    dataSet = dataSet.fromTSDFile(textArea.getText());
                } catch (IOException e1) {
                    System.out.print("");
                }
            }

            if (selectedClassification.equals("Random Classification")) {
                classifier = new RandomClassifier(dataSet, maxIt, interval, continuous, tsd, chart, applicationTemplate);
                runAlg = new Thread(classifier);
                runAlg.start();
            }
        }
        else {
            synchronized (classifier) {
                classifier.notify();
            }
        }


    }

    private Clusterer clusterer;

    public void handleClusteringDisplayRequest(){
        //TODO: hw5
        //ScatterChart and shit
        handleDisplayRequest();
        // TODO: hw5
        scrnshotButton.setDisable(true);
        displayButton.setDisable(true);
        for(Button button: clusteringConfigButtons){
            button.setDisable(true);
        }
        int maxIt = runConfig.getMaxIterations();
        int interval = runConfig.getUpdateInterval();
        int numClusters = runConfig.getNumberOfClusters();
        if (numClusters < 2)
            numClusters = 2;
        else if (numClusters > 4)
            numClusters = 4;
        boolean continuous = runConfig.tocontinue();
        if (continuous ||firstRun) {
            if (((AppActions) applicationTemplate.getActionComponent()).getSavedFile() != null) {
                dataSet = new DataSet();
                try {
                    dataSet = dataSet.fromTSDFile(((AppActions) applicationTemplate.getActionComponent()).getSavedFile().toPath());
                } catch (IOException e) {
                    System.out.print("");
                }
            } else {
                try {
                    dataSet = dataSet.fromTSDFile(textArea.getText());
                } catch (IOException e1) {
                    System.out.print("");
                }
            }
//            try {
//                Thread t = new Thread(Class.forName("Clustering.KMeansClusterer.").getConstructor(DataSet.class, Integer.class, Integer.class, boolean.class, Integer.class,
//                        TSDProcessor.class, LineChart.class, ApplicationTemplate.class));
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
            if (selectedCluster.equals("Random Clustering"))
                clusterer = new RandomClusterer(dataSet, maxIt, interval, continuous, numClusters, tsd, chart, applicationTemplate);
            else if (selectedCluster.equals("KMeans Clustering"))
                clusterer = new KMeansClusterer(dataSet, maxIt, interval, continuous, numClusters, tsd, chart, applicationTemplate);
            runAlg = new Thread(clusterer);
            runAlg.start();
        }
        else {
            synchronized (clusterer) {
                clusterer.notify();
            }
        }
    }
    public void isSaved(boolean val){
        scrnshotButton.setDisable(val);
    }
    public String getData(){
        data=textArea.getText();
        return this.data;
    }
}