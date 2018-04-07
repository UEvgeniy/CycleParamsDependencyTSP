package ui;

import io.DatasetLoader;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.TSPReducedMatrix;

import java.io.File;
import java.io.IOException;
import java.util.Observable;

public class Controller {

    // Properties
    private ObjectProperty<File> fLoadDir,fLoadFile, fSaveFile;
    private BooleanProperty subfolders;
    private DatasetLoader<TSPReducedMatrix> datasetLoader;
    private IntegerProperty loadIndex;

    // Combo Boxes
    @FXML
    public ComboBox<String> cbLoadedData, cbTypeView, cbCycleParam, cbCorrelation;
    @FXML
    public Button btnBrowseLoad, btnBrowseSave, btnLoad;
    @FXML
    public TextField tfLoad, tfSave, tfLoadCompl;
    @FXML
    public Pane paneFolder, paneFile;
    @FXML
    public Label lblFolder, lblFile;
    @FXML
    public CheckBox cbSubfolders;

    @FXML
    public void initialize(){
        // Comboboxes
        cbLoadedData.getItems().addAll(
                "Txt matrixes files", "Obj matrixes files", "Obj dataset file");
        cbCorrelation.getItems().addAll("Pearson", "Spearman");
        cbCycleParam.getItems().addAll("Cycle length", "Number of cycles");
        cbTypeView.getItems().addAll("Table", "List");

        for (ComboBox cb : new ComboBox[]{cbLoadedData, cbTypeView, cbCycleParam, cbCorrelation}){
            cb.getSelectionModel().selectFirst();
        }

        // Binding data
        fLoadDir = new SimpleObjectProperty<>();
        fSaveFile = new SimpleObjectProperty<>();
        fLoadFile = new SimpleObjectProperty<>();
        subfolders = new SimpleBooleanProperty();
        loadIndex = new SimpleIntegerProperty();

        tfLoad.textProperty().bind(fLoadDir.asString());
        tfLoadCompl.textProperty().bind(fLoadFile.asString());
        tfSave.textProperty().bind(fSaveFile.asString());
        subfolders.bind(cbSubfolders.selectedProperty());
        loadIndex.bind(cbLoadedData.getSelectionModel().selectedIndexProperty());
        lblFolder.textProperty().bind(
                Bindings.format("%s:",cbLoadedData.getSelectionModel().selectedItemProperty())
        );
    }

    @FXML
    public void onBrowseFolder(ActionEvent actionEvent) {

        if (loadIndex.get() == 2){
            browseFile(
                    new FileChooser.ExtensionFilter("Dataset file", "*.ds"),
                    fLoadDir,
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    true);
        }
        else {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setInitialDirectory(new File("D://VKR/data"));
            fLoadDir.setValue(
                    dirChooser.showDialog(((Node) actionEvent.getSource()).getScene().getWindow())
            );
        }

    }

    public void onLoadedDataSelected(ActionEvent actionEvent) {
        fLoadDir.setValue(null);
        fLoadFile.setValue(null);
    }

    public void onBrowseFile(ActionEvent actionEvent) {

        Window win = ((Node) actionEvent.getSource()).getScene().getWindow();
        Button sourse = (Button) actionEvent.getSource();

        if (sourse == btnBrowseLoad){
            browseFile(
                    new FileChooser.ExtensionFilter("TXT file", "*.txt"),
                    fLoadFile,
                    win,
                    true
            );

        }
        else if (sourse == btnBrowseSave){
            browseFile(
                    new FileChooser.ExtensionFilter("CSV file", "*.csv"),
                    fSaveFile,
                    win,
                    false
            );
        }
    }

    private void browseFile(FileChooser.ExtensionFilter filter, ObjectProperty<File> file, Window win, boolean isOpen){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D://VKR/data"));
        fileChooser.getExtensionFilters().add(filter);

        file.setValue(isOpen ?
                fileChooser.showOpenDialog(win) :
                fileChooser.showSaveDialog(win));
    }

    public void onLoadData(ActionEvent actionEvent) {

        int selected = loadIndex.get();
        switch (selected){
            case 0:
                //datasetLoader =
                break;
            case 1:
                break;
            case 2:
                break;
        }

    }
}
