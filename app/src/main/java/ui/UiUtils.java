package ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

class UiUtils {

    static void alert(Alert.AlertType type, String header, String text, ButtonType... buttons){
        Alert al = new Alert(type, text, buttons);
        al.setHeaderText(header);
        al.showAndWait();
    }
    static FileChooser.ExtensionFilter initFilter(String desc, String... ext){
        return new FileChooser.ExtensionFilter(desc, ext);
    }

}
