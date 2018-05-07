package actions;


import dataprocessors.TSDProcessor;
import javafx.geometry.Point2D;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppActionsTest {

    private String points= "";
    private ArrayList<String> labels= new ArrayList<>();


    @Test
        public void stringCheck() throws TSDProcessor.ThreeTabsDoesntExist, TSDProcessor.TwoPointsDoesntExist, TSDProcessor.WrongPointException {
            AppActions.stringCheck("@instance1\tlabel1\t4,4");
        }
        @Test(expected = TSDProcessor.ThreeTabsDoesntExist.class)
        public void stringCheck2() throws TSDProcessor.ThreeTabsDoesntExist, TSDProcessor.TwoPointsDoesntExist, TSDProcessor.WrongPointException {
            AppActions.stringCheck("@instance1\tlabel1");
        }

        @Test(expected = TSDProcessor.TwoPointsDoesntExist.class)
        public void stringCheck3() throws TSDProcessor.ThreeTabsDoesntExist, TSDProcessor.TwoPointsDoesntExist, TSDProcessor.WrongPointException {
            AppActions.stringCheck("@instance1\tlabel1\t3.2");
        }
        @Test(expected = TSDProcessor.WrongPointException.class)
        public void stringCheck4() throws TSDProcessor.ThreeTabsDoesntExist, TSDProcessor.TwoPointsDoesntExist, TSDProcessor.WrongPointException {
            AppActions.stringCheck("@instance1\tlabel1\te,l");
        }

    //File is being written to an incorrect file path
    @Test(expected = NullPointerException.class)
    public void savetoFileTest1() throws IOException {
        File savedFile = new File((String) null);
        String path = "newfile.tsd";
        Path savedFilePath = Paths.get(path);
        FileWriter fW = new FileWriter(savedFile);
        fW.write("HI my name");
        fW.close();
    }

    @Test(expected = Exception.class)
    public void savetoFileTest2() throws Exception{
        String data = "instance1\tlabel1\t3.3,4.5";
        for (String line : data.split("\n")) {
            List<String> list = Arrays.asList(line.split("\t"));
            String name = checkedname(list.get(0));
            String label = checkedLabel(list.get(1));
            String[] pair = list.get(2).split(",");
            Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
        }
    }

    @Test
    public void savetoFileTest3() throws IOException {
        File savedFile = new File("newfile.tsd");
        String path = "sample-file.tsd";
        Path savedFilePath = Paths.get(path);
        FileWriter fW = new FileWriter(savedFile);
        fW.write("HI my name");
        fW.close();
        FileReader fR = new FileReader(savedFile);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(savedFile));
        StringBuilder sB= new StringBuilder();
        String text;
        int i=0;
        while ((text = bufferedReader.readLine()) != null) {
            sB.append(text);
        }
        Assert.assertEquals(sB.toString(), "HI my name");
    }
    private String checkedLabel(String label) {
        if(!labels.contains(label))
            labels.add(label);
        return label;
    }
    private String checkedname(String name) throws Exception {
        if (!name.startsWith("@"))
            throw new Exception();
        if(points.contains(name)){
            throw new Exception();
        }
        points += " " + name;
        return name;
    }
}