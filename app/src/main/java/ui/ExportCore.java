package ui;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Window;

import java.io.File;

public class ExportCore {


    static EventHandler<ActionEvent> onBrowseExport(ExportDataType type,
                                                        ObjectProperty<File> fSaveData){
        return event -> {
            Window win = ((Node) event.getSource()).getScene().getWindow();

            switch (type){
                case DISTR_CYCLE_LEN:
                    UiUtils.browseFile(UiUtils.FC_CSV, fSaveData, win, false);
                    break;
                case DISTR_CYCLE_TO_CITY:
                    UiUtils.browseFile(UiUtils.FC_CSV, fSaveData, win, false);
                    break;
                case SERIALIZE_MATRIXES:
                    UiUtils.browseDir(fSaveData, win);
                    break;
                case SERIALIZE_DATASET:
                    UiUtils.browseFile(UiUtils.FC_DC, fSaveData, win, false);
                    break;
                case LIST:
                    UiUtils.browseFile(UiUtils.FC_TXT, fSaveData, win, false);
                    break;
                case TABLE_FUNCTIONS:
                    UiUtils.browseFile(UiUtils.FC_CSV, fSaveData, win, false);
            }

        };
    }
}
