package dataprocessors;

import javafx.geometry.Point2D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TSDProcessorTest {

    private String points= "";
    private ArrayList<String> labels= new ArrayList<>();

    @Test(expected = TSDProcessor.InvalidDataNameException.class)
    public void processStringTest1() throws Exception {
        String test = "";
        TSDProcessor tsd = new TSDProcessor();
        for (String line : test.split("\n")) {
            List<String> list = Arrays.asList(line.split("\t"));
            String name = checkedname(list.get(0));
            String label = checkedLabel(list.get(1));
            String[] pair = list.get(2).split(",");
            Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
        }
    }
    private String checkedLabel(String label) {
        if(!labels.contains(label))
            labels.add(label);
        return label;
    }

    private String checkedname(String name) throws TSDProcessor.InvalidDataNameException, TSDProcessor.RepeatNameException {
        if (!name.startsWith("@"))
            throw new TSDProcessor.InvalidDataNameException(name);
        if(points.contains(name)){
            throw new TSDProcessor.RepeatNameException(name);
        }
        points += " " + name;
        return name;
    }
}