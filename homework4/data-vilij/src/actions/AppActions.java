package actions;

import dataprocessors.TSDProcessor;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Path;

import static settings.AppPropertyTypes.*;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;
    private Object obj;

    /** Path to the data file currently active. */
    Path dataFilePath;
    File savedFile;
    boolean save=false;
    private Label dataLabel;
    private static final String NEWLINE= "\n";
    private static final String DASH = "-";

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }
    TSDProcessor LabelProcessor= new TSDProcessor();

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        AppUI app = (AppUI) applicationTemplate.getUIComponent();
        app.getTextArea().setDisable(false);
        if(save==true){
            app.clear();
            app.getVB().getChildren().clear();
            app.getVB().getChildren().add(app.getTextArea());
            app.getVB().getChildren().add(app.getHB());
            savedFile=null;
            save=false;
        }
        else {
            if (app.getHasNewText()) {
                if (promptToSave()) {
                    app.clear();
                    app.getVB().getChildren().clear();
                    app.getVB().getChildren().add(app.getTextArea());
                    app.getVB().getChildren().add(app.getHB());
                    savedFile = null;

                } else ;
            } else {
                app.clear();
                app.getVB().getChildren().clear();
                app.getVB().getChildren().add(app.getTextArea());
                app.getVB().getChildren().add(app.getHB());
            }
            savedFile = null;
        }
    }
    public void handleValidationRequest(){
        AppUI app = (AppUI) applicationTemplate.getUIComponent();
        LabelProcessor = new TSDProcessor();
        try {
            LabelProcessor.processString(app.getTextArea().getText());
        } catch (Exception e) {
            ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(ERROR_DATA.name()));
        }
        app.getVB().getChildren().removeAll(app.getLabel(),app.getChoiceBox());
        createLabel();
        app.setLabel(dataLabel);
        app.getVB().getChildren().add(app.getLabel());
        if (LabelProcessor.getLabels().size() >1)
            app.getChoiceBox().setItems(FXCollections.observableArrayList("Select Algorithm Type",
                    "Clustering", "Classification"));
        else
            app.getChoiceBox().setItems(FXCollections.observableArrayList("Select Algorithm Type",
                    "Clustering"));

        app.getChoiceBox().getSelectionModel().selectFirst();
        app.getVB().getChildren().add(app.getChoiceBox());
        //       app.setWorkspaceActions();

    }


    @Override
    public void handleSaveRequest() {
        try {
            AppUI app = (AppUI) applicationTemplate.getUIComponent();
            savetoFile();
            save=true;
        } catch (IOException e) {
            ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
        }
    }
    public void createLabel() {
        StringBuilder infoLabel = new StringBuilder();
        infoLabel.append(LabelProcessor.getCounter() +
                applicationTemplate.manager.getPropertyValue(INSTANCE_COUNT.name()) +
                LabelProcessor.getLabels().size() +
                applicationTemplate.manager.getPropertyValue(LABEL_COUNT.name()) + NEWLINE);
        if(savedFile != null)
            infoLabel.append(savedFile);
        else
            infoLabel.append(applicationTemplate.manager.getPropertyValue(NO_FILE_FOUND.name()));
        infoLabel.append(applicationTemplate.manager.getPropertyValue(LABEL_LIST.name()) + NEWLINE);
        for (String label:LabelProcessor.getLabels()) {
            String line = DASH+label+NEWLINE;
            infoLabel.append(line);
        }
        dataLabel = new Label(infoLabel.toString());
    }

    @Override
    public void handleLoadRequest(){
        // TODO: NOT A PART OF HW 1
        AppUI app = (AppUI) applicationTemplate.getUIComponent();
        try {
            loadFile();
            app.getVB().getChildren().clear();
            app.getTextArea().setDisable(true);
            app.getVB().getChildren().add(app.getTextArea());
            handleValidationRequest();

        } catch (Exception e) {
            ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(ERROR_DATA.name()));
        }
        app.isSaved(true);

    }

    public File getSavedFile(){ return savedFile; }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1

        AppUI app= (AppUI) applicationTemplate.getUIComponent();
        Stage stage= app.getPrimaryWindow();
        if (app.getHasNewText()) {
            if(promptToSave())
                stage.close();
            else
                return;
        }
        else
            stage.close();


    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
        AppUI app= (AppUI) applicationTemplate.getUIComponent();
        WritableImage image= app.getScatterChart().snapshot(new SnapshotParameters(), null);
        File file = new File("chart.png");
        ImageIO.write(SwingFXUtils.fromFXImage(image,null), "png", file);

    }


    private void loadFile() throws TSDProcessor.InvalidDataNameException {
        AppUI app = (AppUI) applicationTemplate.getUIComponent();
        LabelProcessor = new TSDProcessor();
        FileChooser fC = new FileChooser();
        FileChooser.ExtensionFilter eF = new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()));
        fC.getExtensionFilters().add(eF);
        TSDProcessor tsd= new TSDProcessor();
        UITemplate template = (UITemplate) applicationTemplate.getUIComponent();
        savedFile=fC.showOpenDialog(template.getPrimaryWindow());


        if(savedFile!=null){
            StringBuilder stringBuffer = new StringBuilder();
            StringBuilder sB2= new StringBuilder();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(savedFile));
                String text;

                while ((text = bufferedReader.readLine()) != null) {
                    stringBuffer.append(text);
                    stringBuffer.append("\n");
                }
                bufferedReader.close();
                LabelProcessor.processString(stringBuffer.toString());
                app.setTextArea(stringBuffer.toString());
                app.isSaved(false);
            } catch (FileNotFoundException e) {
                ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));

            } catch (IOException e) {
                ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));

            } catch (Exception e){
                ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                String errorLine= applicationTemplate.manager.getPropertyValue(ERROR_AT_LINE.name()) +""+ tsd.getCounter();
                errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()), errorLine);
            }

        }

    }
    private void savetoFile() throws IOException {
        AppUI app = (AppUI) applicationTemplate.getUIComponent();
        TSDProcessor tsd= new TSDProcessor();
        FileChooser fC = new FileChooser();
        boolean hadError=false;
        try{
            String data= app.getData();
            tsd.processString(data);
        }catch(Exception e){
            ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            String errorLine= applicationTemplate.manager.getPropertyValue(ERROR_AT_LINE.name()) +""+ tsd.getCounter();
            errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()), errorLine);
            hadError= true;
        }
        if (hadError)
            return;

        FileChooser.ExtensionFilter eF = new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()));
        fC.getExtensionFilters().add(eF);
        UITemplate template = (UITemplate) applicationTemplate.getUIComponent();
        if(savedFile==null) {
            savedFile = fC.showSaveDialog(template.getPrimaryWindow());
            save = true;
        }
        if(savedFile!=null)
            try {
                FileWriter fW = new FileWriter(savedFile);
                fW.write(app.getData());
                fW.close();
                save=true;
                app.isSaved(true);
            } catch (IOException e1) {
                ErrorDialog errorDialogue = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
            } catch(Exception e2){
                ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                String errorLine= applicationTemplate.manager.getPropertyValue(ERROR_AT_LINE.name()) +""+ tsd.getCounter();
                errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()), errorLine);
            }
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() {
        // TODO for homework 1
        UITemplate template = (UITemplate) applicationTemplate.getUIComponent();
        ConfirmationDialog saveDialogue= (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
        saveDialogue.show(applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()),
                applicationTemplate.manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
        if(saveDialogue.getSelectedOption() == ConfirmationDialog.Option.CANCEL) {
            return false;
        }
        else if(saveDialogue.getSelectedOption()== ConfirmationDialog.Option.YES) {
            try {
                savetoFile();
            } catch (IOException e) {
                ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                        applicationTemplate.manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
            }
        }
        else;
        return true;
    }
}