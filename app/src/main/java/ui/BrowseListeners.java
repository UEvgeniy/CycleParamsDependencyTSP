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
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ui.ExportDataType.*;
import static ui.ImportDataType.*;


class BrowseListeners {

    private static HashMap<DataType, BiConsumer<Window, ObjectProperty<File>>> actions;

    static {
        actions = new HashMap<>();
        actions.put(TXT_MATRIXES, (window, file) -> UiUtils.browseDir(file, window));
        actions.put(OBJ_MATRIXES, (window, file) -> UiUtils.browseDir(file, window));
        actions.put(OBJ_DATASET, (window, file) -> UiUtils.browseFile(UiUtils.FC_DC, file, window, true));
        actions.put(TXT_COMPLEXITY, (window, file) -> UiUtils.browseFile(UiUtils.FC_TXT, file, window, true));

        actions.put(DISTR_CYCLE_LEN, (window, file) -> UiUtils.browseFile(UiUtils.FC_CSV, file, window, false));
        actions.put(DISTR_CYCLE_TO_CITY, (window, file) -> UiUtils.browseFile(UiUtils.FC_CSV, file, window, false));
        actions.put(SERIALIZE_MATRIXES, (window, file) -> UiUtils.browseDir(file, window));
        actions.put(SERIALIZE_DATASET, (window, file) -> UiUtils.browseFile(UiUtils.FC_DC, file, window, false));
        actions.put(LIST, (window, file) -> UiUtils.browseFile(UiUtils.FC_TXT, file, window, false));
        actions.put(TABLE_FUNCTIONS, (window, file) -> UiUtils.browseFile(UiUtils.FC_CSV, file, window, false));


    }

    static void browse(DataType type, ActionEvent event, ObjectProperty<File> file){
        actions.get(type).accept(((Node) event.getSource()).getScene().getWindow(), file);
    }
}
