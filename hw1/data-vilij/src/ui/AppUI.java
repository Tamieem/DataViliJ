package ui;

import actions.AppActions;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;

import java.io.IOException;

import static settings.AppPropertyTypes.*;
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
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    String data;

    public boolean getHasNewText(){
        return this.hasNewText;
    }



    public ScatterChart<Number, Number> getChart() { return chart; }

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
                e1.printStackTrace();
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
        textArea.clear();
        chart.getData().clear();
        newButton.setDisable(true);
        saveButton.setDisable(true);
        hasNewText=false;

    }

    private void layout() {
        // TODO for homework 1
        chart = new ScatterChart<Number, Number>(new NumberAxis(),new NumberAxis());
        chart.setTitle(applicationTemplate.manager.getPropertyValue(CHART_TITLE.name()));
        BorderPane bp = new BorderPane();
        appPane.getChildren().add(bp);
        bp.setRight(chart);
        VBox vB= new VBox();
        textArea = new TextArea();
        displayButton = new Button(applicationTemplate.manager.getPropertyValue(DISPLAY.name()));
        Label label = new Label(applicationTemplate.manager.getPropertyValue(TEXT_AREA.name()));
        vB.getChildren().add(label);
        vB.getChildren().add(textArea);
        vB.getChildren().add(displayButton);
        bp.setLeft(vB);
        displayButton.setOnAction(e -> handleDisplayRequest());
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



    }
    public void handleDisplayRequest() { // CUSTOM METHOD
        AppData appData=new AppData(applicationTemplate);
        TSDProcessor tsd = new TSDProcessor();
        chart.getData().clear();
        data= textArea.getText();
         try {
           tsd.processString(data);
        } catch (Exception E) {
                 ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                  applicationTemplate.manager.getPropertyValue(ERROR_DATA.name()));
         }

      tsd.toChartData(chart);
    }
}
