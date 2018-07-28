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

    //private static final String INITIAL_BROWSE_PATH = "C:\\Users\\Urnyshev-ED\\train";
    private static final String INITIAL_BROWSE_PATH = "D://vkr//data";

    static class Alerts{
        static void infoCancel(){
            alert(Alert.AlertType.INFORMATION, "Loading data was cancelled",
                    "", ButtonType.OK);
        }

        static void errorFileInvalid(String message){
            alert(Alert.AlertType.ERROR, "Some files are invalid",
                    "Please, check the files: " + message, ButtonType.OK);
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

        static void errorNothingToLoad(String datasetName){
            alert(Alert.AlertType.ERROR, "Dataset is empty",
                    "Dataset ".concat(datasetName).concat(" has no elements. Experiments cannot be executed"), ButtonType.OK);
        }

        static void errorIO(String message){
            alert(Alert.AlertType.ERROR, "Problems with file",
                    "result cannot be saved. " + message, ButtonType.OK);
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
