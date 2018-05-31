package ui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

class UiUtils {

    static final FileChooser.ExtensionFilter FC_TXT =
            UiUtils.initFilter("TXT file", "*.txt");
    static final FileChooser.ExtensionFilter FC_DC =
            UiUtils.initFilter("Dataset file", "*.ds");
    static final FileChooser.ExtensionFilter FC_CSV =
            UiUtils.initFilter("CSV file", "*.csv");

    //private static final String INITIAL_BROWSE_PATH = "C://"; todo path
    private static final String INITIAL_BROWSE_PATH = "D://vkr//data";

    static class Alerts{
        static void infoCancel(){
            alert(Alert.AlertType.INFORMATION, "Loading data was cancelled",
                    "", ButtonType.OK);
        }

        static void errorInvalidPath(){
            alert(Alert.AlertType.ERROR, "Some paths are invalid",
                    "Please, check all paths", ButtonType.OK);
        }

        static void errorNoData(){
            alert(Alert.AlertType.ERROR, "No data to save",
                    "Please, load data first", ButtonType.OK);
        }

        static void infoSuccessExport(File file){
            alert(Alert.AlertType.INFORMATION, "Data saved",
                    "Data saved in file " + file.getName(), ButtonType.OK);
        }

        static void infoSuccessImport(int elementsNumber, double seconds){
            alert(Alert.AlertType.INFORMATION, "Datasets successfully loaded",
                    String.format("%d elements loaded for %2$,.2f sec", elementsNumber, seconds),
                    ButtonType.OK);
        }

        static void errorNullPaths(){
            UiUtils.alert(Alert.AlertType.ERROR, "Some paths are null",
                    "Please, fill all the paths", ButtonType.OK);
        }

        static void errorNothingToLoad(){
            alert(Alert.AlertType.ERROR, "No data to load",
                    "...", ButtonType.OK);
        }

        static void infoSuccessExperiment(double value){
            alert(Alert.AlertType.INFORMATION, "Experiments compeleted",
                    String.format("The result correlation is %1$,.2f", value),
                    ButtonType.OK);
        }
    }


    private static void alert(Alert.AlertType type, String header, String text, ButtonType... buttons){
        Alert al = new Alert(type, text, buttons);
        al.setHeaderText(header);
        al.showAndWait();
    }

    /**
     * @param isOpen true - open file. False - save file dialog
     */
    static void browseFile(FileChooser.ExtensionFilter filter, ObjectProperty<File> file,
                            Window win, boolean isOpen){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(INITIAL_BROWSE_PATH));
        fileChooser.getExtensionFilters().add(filter);

        file.setValue(isOpen ?
                fileChooser.showOpenDialog(win) :
                fileChooser.showSaveDialog(win));
    }
    static void browseDir(ObjectProperty<File> file, Window win){
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(new File(INITIAL_BROWSE_PATH));
        file.setValue(dirChooser.showDialog(win));
    }

    private static FileChooser.ExtensionFilter initFilter(String desc, String... ext){
        return new FileChooser.ExtensionFilter(desc, ext);
    }
}
