package actions;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import vilij.templates.UITemplate;
import vilij.settings.PropertyTypes;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.templates.UITemplate.*;

import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.SAVE_WORK_TITLE;
import static vilij.templates.UITemplate.*;

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

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        AppUI app = (AppUI) applicationTemplate.getUIComponent();
        boolean save=false;
        if(app.getHasNewText()) {
            if (promptToSave() == true)
                app.clear();
            else ;
        }
        else
            app.clear();
    }


    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1

        AppUI app= (AppUI) applicationTemplate.getUIComponent();
        Stage stage= app.getPrimaryWindow();
        if (app.getHasNewText()) {
                if(promptToSave()==true)
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
    }
    private void savetoFile() throws IOException{
        Button saveButton= new Button(applicationTemplate.manager.getPropertyValue(SAVE_WORK_TITLE.name()));
        FileChooser fC = new FileChooser();
        FileChooser.ExtensionFilter eF= new FileChooser.ExtensionFilter(applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                applicationTemplate.manager.getPropertyValue(DATA_FILE_EXT.name()));
        fC.getExtensionFilters().add(eF);
        UITemplate template = (UITemplate) applicationTemplate.getUIComponent();
        fC.showSaveDialog(template.getPrimaryWindow());
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
                 errorDialogue.init((Stage) applicationTemplate.getUIComponent().getPrimaryWindow());
             }
         }
         else;
         return true;
    }
}
