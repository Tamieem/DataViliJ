package dataprocessors;

import com.sun.xml.internal.fastinfoset.util.StringArray;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.templates.ApplicationTemplate;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import static settings.AppPropertyTypes.ERROR_TITLE;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    private ApplicationTemplate applicationTemplate;
    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
        public InvalidDataNameException(int num){
            super(String.format("Invalid name at line " +num));
        }

    }
    public static class RepeatNameException extends Exception{
        private static final String NAME_REPEAT_MSG = "This instance has been repeated before" ;
        public RepeatNameException(String name){
            super(String.format("Invalid name '%s' ."+ NAME_REPEAT_MSG, name));
        }
        public RepeatNameException(int num){
            super(String.format("Repeated name at line " + num));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;
    String points ="";
    StringArray names= new StringArray();
    int counter=0;
    ArrayList yComponent= new ArrayList<Double>();



    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }
    public int getCounter(){
        return this.counter;
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      counter++;
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      yComponent.add(point.getY());
                      names.add(name);
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                  } catch (RepeatNameException e) {
                      ErrorDialog errorDialogue = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                      String errorLine = new RepeatNameException(counter).toString();
                      errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()), errorLine);
                      hadAnError.set(true);
                  } catch (InvalidDataNameException e) {
                      ErrorDialog errorDialogue = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                      String errorLine = new InvalidDataNameException(counter).toString();
                      errorDialogue.show(applicationTemplate.manager.getPropertyValue(ERROR_TITLE.name()), errorLine);
                      hadAnError.set(true);
                  }
              });
    }
    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    public void toChartData(XYChart<Number, Number> chart, XYChart<Number, Number> lineChart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        // Line Chart data
        int i=0;
        double j=0;
        for(i=0; i< yComponent.size();i++){
            j+= (double)yComponent.get(i);
        }
        j= j/i; // average value;
        final double average= j;
        //ScatterChart
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            XYChart.Series<Number, Number> series1= new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
                series1.getData().add(new XYChart.Data<>(point.getX(), average));
            });
            chart.getData().add(series);
            lineChart.getData().add(series1);

        }
        // ToolTip
        i=0;
        for (XYChart.Series<Number, Number> s : chart.getData()) {
            for (XYChart.Data<Number, Number> d : s.getData()) {
                Tooltip.install(d.getNode(), new Tooltip(names.get(i)));
                d.getNode().setOnMouseEntered(event -> chart.setCursor(Cursor.CROSSHAIR));
                d.getNode().setOnMouseExited(event -> chart.setCursor(Cursor.DEFAULT));
                i++;
            }
        }

    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException, RepeatNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        if(points.contains(name)){
            throw new RepeatNameException(name);
        }
        points += " " + name;
        return name;
    }
}
