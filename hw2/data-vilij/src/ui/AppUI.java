package ui;

import actions.AppActions;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import settings.AppPropertyTypes;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;

import java.io.IOException;

import static javafx.scene.input.KeyCode.X;
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

    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private String                       scrnshotPath; // path to screenshot button
    private String                       cssPath;
    private String                       lineChartPath;
    private String                       textAreaPath;
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private LineChart<Number, Number>    lineChart;
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                       data;
    private CheckBox                     cb1;

    public boolean getHasNewText(){
        return this.hasNewText;
    }



    public ScatterChart<Number, Number> getScatterChart() { return chart; }
    public LineChart<Number, Number> getLineChart(){ return lineChart; }

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
        newButton = setToolbarButton(newiconPath, manager.getPropertyValue(NEW_TOOLTIP.name()), true);
        saveButton = setToolbarButton(saveiconPath, manager.getPropertyValue(SAVE_TOOLTIP.name()), true);
        loadButton = setToolbarButton(loadiconPath, manager.getPropertyValue(LOAD_TOOLTIP.name()), false);
        printButton = setToolbarButton(printiconPath, manager.getPropertyValue(PRINT_TOOLTIP.name()), true);
        exitButton = setToolbarButton(exiticonPath, manager.getPropertyValue(EXIT_TOOLTIP.name()), false);
        scrnshotButton= setToolbarButton(scrnshotPath,manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar = new ToolBar(newButton, saveButton, loadButton, printButton, exitButton, scrnshotButton);
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
        lineChart.getStylesheets().addAll(getClass().getResource(lineChartPath).toExternalForm());
        textArea.getStylesheets().addAll(getClass().getResource(textAreaPath).toExternalForm());
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
        StackPane sp = new StackPane();
        lineChart= new LineChart<Number, Number>(new NumberAxis(),new NumberAxis());
        chart = new ScatterChart<Number, Number>(lineChart.getXAxis(),lineChart.getYAxis());
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(true);
        lineChart.setCreateSymbols(true);
        lineChart.setAlternativeRowFillVisible(false);
        lineChart.setAlternativeColumnFillVisible(false);
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.getXAxis().setVisible(false);
        lineChart.getYAxis().setVisible(false);
        chart.setTitle(applicationTemplate.manager.getPropertyValue(CHART_TITLE.name()));
        sp.getChildren().addAll(lineChart, chart);
        BorderPane bp = new BorderPane();
        appPane.getChildren().add(bp);
        bp.setRight(sp);
        VBox vB= new VBox();
        textArea = new TextArea();
        displayButton = new Button(applicationTemplate.manager.getPropertyValue(DISPLAY.name()));
        Label label = new Label(applicationTemplate.manager.getPropertyValue(TEXT_AREA.name()));
        vB.getChildren().add(label);
        vB.getChildren().add(textArea);
        vB.getChildren().add(displayButton);
        bp.setLeft(vB);
        displayButton.setOnAction(e -> handleDisplayRequest());
        cb1= new CheckBox(applicationTemplate.manager.getPropertyValue(READ_ONLY.name()));
        vB.getChildren().add(cb1);
    }

    private void setWorkspaceActions() {
        // TODO for homework 1
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newButton.setDisable(false);
                saveButton.setDisable(false);
                hasNewText= true;
                displayButton.setDisable(false);
                String data= textArea.getText();
                if(data.isEmpty()){
                    newButton.setDisable(true);
                    saveButton.setDisable(true);
                    displayButton.setDisable(true);
                    hasNewText= false;
                }
            }
        });// Makes New and Save button clickable
        cb1.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                if(cb1.isSelected()){
                    textArea.setDisable(true);
                }
                else if(!cb1.isSelected())
                    textArea.setDisable(false);
            }
        });
        chart.setCursor(Cursor.CROSSHAIR);
        textArea.setPrefRowCount(10);

    }

    public void setTextArea(String text){
        textArea.setText(text);
    }
    public void handleDisplayRequest() { // CUSTOM METHOD
        AppData appData=new AppData(applicationTemplate);
        TSDProcessor tsd = new TSDProcessor();
        chart.getData().clear();
        lineChart.getData().clear();
        data= textArea.getText();
         try {
           tsd.processString(data);
           tsd.toChartData(chart, lineChart);
           scrnshotButton.setDisable(false);
        } catch (Exception E) {
                 ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                  applicationTemplate.manager.getPropertyValue(ERROR_DATA.name()));
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
