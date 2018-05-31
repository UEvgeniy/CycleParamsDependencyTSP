package ui;

import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.Dataset;

import java.io.File;


public class ImportCore {





    static EventHandler<ActionEvent> onBrowseMatrix(ImportDataType type, ObjectProperty<File> fLoadMatrixes){
        return event -> {
            Window win = ((Node) event.getSource()).getScene().getWindow();
            switch (type){
                case TXT_MATRIXES:
                    UiUtils.browseDir(fLoadMatrixes, win);
                    break;
                case OBJ_MATRIXES:
                    UiUtils.browseDir(fLoadMatrixes, win);
                    break;
                case OBJ_DATASET:
                    UiUtils.browseFile(UiUtils.FC_DC, fLoadMatrixes, win, true);
                    break;
            }
        };
    }

    public void onTryCancel(ActionEvent event){
        UiUtils.Alerts.infoCancel();
    }

    static EventHandler<ActionEvent> onBrowseComplexity(ObjectProperty<File> fLoadComplexity){
        return event -> {
            Window win = ((Node) event.getSource()).getScene().getWindow();
            UiUtils.browseFile(UiUtils.FC_TXT, fLoadComplexity, win, true);
        };
    }

}
