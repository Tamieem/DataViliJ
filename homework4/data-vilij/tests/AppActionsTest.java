package actions;

import javafx.geometry.Point2D;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppActionsTest {

    private String points= "";
    private ArrayList<String> labels= new ArrayList<>();


    //File is being written to an incorrect file path
    @Test(expected = IOException.class)
    public void savetoFileTest1() throws IOException {
        File savedFile = new File("\\homework4\\tests\\newfile.tsd");
        String path = "\\homework4\\tests\\missingfile.tsd";
        Path savedFilePath = Paths.get(path);
        FileWriter fW = new FileWriter(savedFile);
        fW.write(path);
        fW.close();
    }

    // Invalid text being processed
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


//    public void savetoFileTest3() throws IOException{
//        File savedFile = new File("\\homework4\\tests\\newfile.tsd");
//        String path = "\\homework4\\tests\\sample-file.tsd";
//        Path savedFilePath = Paths.get(path);
//        FileWriter fW = new FileWriter(savedFile);
//        fW.write(path);
//        fW.close();
//        Assert.assertEquals(savedFile, savedFilePath.toFile());
//
//    }

}