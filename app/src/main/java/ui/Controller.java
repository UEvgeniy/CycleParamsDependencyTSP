package ui;

import control.ReducingOrder;
import control.TSPConverter;
import io.ObjReducedDatasetLoader;
import io.ObjReducedMatrixesLoader;
import io.TxtMatrixLoader;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.Complexity;
import model.Dataset;
import model.TSPReducedMatrix;

import java.io.File;

public class Controller {

    // Properties
    private ObjectProperty<File> fLoadMatrixes, fLoadComplexity, fSaveData;
    private BooleanProperty subfolders;
    private Dataset<TSPReducedMatrix> reducedMatrixDataset;
    private Dataset<Complexity> complexityDataset;
    private IntegerProperty loadIndex;
    private  Task<Dataset<TSPReducedMatrix>> task;

    // Combo Boxes
    @FXML
    public ComboBox<String> cbLoadedData, cbTypeView, cbCycleParam, cbCorrelation;
    @FXML
    public Button btnBrowseLoad, btnBrowseSave, btnLoad, btnLoadCompl, cancelBtn;
    @FXML
    public TextField tfLoad, tfSave, tfLoadCompl;
    @FXML
    public Pane paneFolder, paneFile;
    @FXML
    public Label lblFolder, lblFile, lblNumEl;
    @FXML
    public CheckBox cbSubfolders;
    @FXML
    public ProgressBar progressBar;

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
        fLoadMatrixes = new SimpleObjectProperty<>();
        fSaveData = new SimpleObjectProperty<>();
        fLoadComplexity = new SimpleObjectProperty<>();
        subfolders = new SimpleBooleanProperty();
        loadIndex = new SimpleIntegerProperty();
        reducedMatrixDataset = new Dataset<>();

        tfLoad.textProperty().bind(fLoadMatrixes.asString());
        tfLoadCompl.textProperty().bind(fLoadComplexity.asString());
        tfSave.textProperty().bind(fSaveData.asString());
        subfolders.bind(cbSubfolders.selectedProperty());
        loadIndex.bind(cbLoadedData.getSelectionModel().selectedIndexProperty());
        lblFolder.textProperty().bind(
                Bindings.format("%s:",cbLoadedData.getSelectionModel().selectedItemProperty())
        );
        lblNumEl.textProperty().bind(
                Bindings.format("%d matrixes loaded", reducedMatrixDataset.getKeys().size())
        );
    }

    @FXML
    public void onBrowseFolder(ActionEvent actionEvent) {

        if (loadIndex.get() == 2){
            browseFile(
                    new FileChooser.ExtensionFilter("Dataset file", "*.ds"),
                    fLoadMatrixes,
                    ((Node) actionEvent.getSource()).getScene().getWindow(),
                    true);
        }
        else {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setInitialDirectory(new File("D://VKR/data"));
            fLoadMatrixes.setValue(
                    dirChooser.showDialog(((Node) actionEvent.getSource()).getScene().getWindow())
            );
        }

    }

    public void onLoadedDataSelected(ActionEvent actionEvent) {
        fLoadMatrixes.setValue(null);
    }

    public void onBrowseFile(ActionEvent actionEvent) {

        Window win = ((Node) actionEvent.getSource()).getScene().getWindow();
        Button sourse = (Button) actionEvent.getSource();

        if (sourse == btnBrowseLoad){
            browseFile(
                    new FileChooser.ExtensionFilter("TXT file", "*.txt"),
                    fLoadComplexity,
                    win,
                    true
            );

        }
        else if(sourse == btnLoadCompl){
            browseFile(
                    new FileChooser.ExtensionFilter("TXT file", "*.txt"),
                    fLoadComplexity,
                    win,
                    true
            );
        }
        else if (sourse == btnBrowseSave){
            browseFile(
                    new FileChooser.ExtensionFilter("CSV file", "*.csv"),
                    fSaveData,
                    win,
                    false
            );
        }
    }

    private void browseFile(FileChooser.ExtensionFilter filter, ObjectProperty<File> file,
                            Window win, boolean isOpen){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D://VKR/data"));
        fileChooser.getExtensionFilters().add(filter);

        file.setValue(isOpen ?
                fileChooser.showOpenDialog(win) :
                fileChooser.showSaveDialog(win));
    }

    public void onLoadData(ActionEvent actionEvent) {
        long start = System.currentTimeMillis();

        // Inialize tasks
        try {
            initTask();
        }
        catch (NullPointerException e){
            Util.alert(
                    Alert.AlertType.ERROR,
                    "Some paths are null",
                    "Please, fill all the paths",
                    ButtonType.OK);
            return;
        }

        // Bind task to progress bar
        progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded((e) -> {
            cancelBtn.setVisible(false);
            reducedMatrixDataset = task.getValue();
            Util.alert(
                    Alert.AlertType.INFORMATION,
                    "Datasets successfully loaded",
                    String.format("%d elements loaded for %2$,.2f sec",
                            reducedMatrixDataset.getKeys().size(),
                            (double)(System.currentTimeMillis() - start) / 1000),
                    ButtonType.OK);
        });

        new Thread(task).start();
        cancelBtn.setVisible(true);
    }

    private void initTask(){
        switch (loadIndex.get()){
            case 0:
                task = new TxtMatrixLoader(fLoadMatrixes.get(), subfolders.get()){
                  @Override
                  protected Dataset<TSPReducedMatrix> call() throws Exception {
                      return TSPConverter.toReducedDataset(load(), ReducingOrder.RowsColumns);
                  }
                };
                break;
            case 1:
                task = new ObjReducedMatrixesLoader(fLoadMatrixes.get(), subfolders.get()){
                    @Override
                    protected Dataset<TSPReducedMatrix> call() throws Exception {
                        return load();
                    }
                };
                break;
            case 2:
                task = new ObjReducedDatasetLoader(fLoadMatrixes.get(), subfolders.get()){
                    @Override
                    protected Dataset<TSPReducedMatrix> call() throws Exception {
                        return load();
                    }
                };
                break;
        }
        // Add handlers
        task.setOnFailed(event -> Util.alert(
                Alert.AlertType.ERROR,
                "Some paths are invalid",
                "Please, check all paths",
                ButtonType.OK));

        task.setOnCancelled(
                event -> {
                    cancelBtn.setVisible(false);
                    progressBar.progressProperty().unbind();
                    progressBar.setProgress(0);
                    Util.alert(
                            Alert.AlertType.INFORMATION,
                            "Loading data was cancelled",
                            "",
                            ButtonType.OK);
                }
        );
    }

    public void onSaveDataView(ActionEvent actionEvent) {
        int a = 12;
    }

    public void onCancel(ActionEvent actionEvent) {
        if (task.isRunning())
            task.cancel();
        cancelBtn.setVisible(false);
    }


    static class Util{
        static void alert(Alert.AlertType type, String header, String text, ButtonType... buttons){


            Alert al = new Alert(type, text, buttons);
            al.setHeaderText(header);
            al.showAndWait();
        }
    }
}
