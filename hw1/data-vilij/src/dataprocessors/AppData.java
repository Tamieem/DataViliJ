package dataprocessors;

import javafx.scene.chart.Chart;
import javafx.stage.Stage;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.templates.ApplicationTemplate;

import java.nio.file.Path;

import static settings.AppPropertyTypes.ERROR_DATA;
import static settings.AppPropertyTypes.ERROR_TITLE;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
    }

    public void loadData(String dataString) throws Exception {
        // TODO: HW 1
        TSDProcessor tsd = new TSDProcessor();
        AppUI appUI= (AppUI) applicationTemplate.getUIComponent();
        try {
            tsd.processString(dataString);
        } catch (Exception E) {

            ErrorDialog errorDialogue= (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()),
                    applicationTemplate.manager.getPropertyValue(ERROR_DATA.name()));
            errorDialogue.init((Stage) applicationTemplate.getUIComponent().getPrimaryWindow());

        }
        displayData();
    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
