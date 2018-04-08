package ui;

import control.DataBinder;
import control.ReducingOrder;
import control.TSPConverter;
import data_view.DataView;
import io.ObjReducedDatasetLoader;
import io.ObjReducedMatrixesLoader;
import io.TxtComplexityLoader;
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
import model.BindedData;
import model.Complexity;
import model.Dataset;
import model.TSPReducedMatrix;

import java.io.File;
import java.io.PrintStream;

public class Controller {


    /**
     *  Class Util with helping methods
     */
    static class Util{
        static void alert(Alert.AlertType type, String header, String text, ButtonType... buttons){
            Alert al = new Alert(type, text, buttons);
            al.setHeaderText(header);
            al.showAndWait();
        }
    }

    // Properties
    private ObjectProperty<File> fLoadMatrixes, fLoadComplexity, fSaveData;
    private BooleanProperty subfolders;
    private Dataset<TSPReducedMatrix> reducedMatrixDataset;
    private Dataset<Complexity> complexityDataset;
    private IntegerProperty loadIndex, saveIndex, size;
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

    /**
     * Initialize operations
     */
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
        saveIndex = new SimpleIntegerProperty();
        reducedMatrixDataset = new Dataset<>();
        size = new SimpleIntegerProperty(0);

        tfLoad.textProperty().bind(fLoadMatrixes.asString());
        tfLoadCompl.textProperty().bind(fLoadComplexity.asString());
        tfSave.textProperty().bind(fSaveData.asString());
        subfolders.bind(cbSubfolders.selectedProperty());
        loadIndex.bind(cbLoadedData.getSelectionModel().selectedIndexProperty());
        saveIndex.bind(cbTypeView.getSelectionModel().selectedIndexProperty());
        lblFolder.textProperty().bind(
                Bindings.format("%s:",cbLoadedData.getSelectionModel().selectedItemProperty())
        );
        lblNumEl.textProperty().bind(
                Bindings.format("%d matrixes loaded", size)
        );
    }

    /**
     * Browse button
     */
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
            FileChooser.ExtensionFilter filter =
                    saveIndex.get() == 0 ?
                            new FileChooser.ExtensionFilter("CSV file", "*.csv") :
                            new FileChooser.ExtensionFilter("TXT file", "*.txt");
            browseFile(
                    filter,
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

    /**
     * ComboBox selected item
     */
    public void onLoadedDataSelected(ActionEvent actionEvent) {
        fLoadMatrixes.setValue(null);
    }

    /**
     * Load data from paths
     */
    public void onLoadData(ActionEvent actionEvent) {
        long start = System.currentTimeMillis();

        Task<Dataset<Complexity>> complLoader = new Task<Dataset<Complexity>>() {
            @Override
            protected Dataset<Complexity> call() throws Exception {
                return new TxtComplexityLoader(fLoadComplexity.get()).load();
            }
        };
        addTaskHandlers(complLoader, true);
        complLoader.setOnSucceeded(event -> {
            complexityDataset = complLoader.getValue();
        });
        new Thread(complLoader).start();

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
            size.setValue(reducedMatrixDataset.getKeys().size());
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
    public void onCancel(ActionEvent actionEvent) {
        if (task != null && task.isRunning())
            task.cancel();
        cancelBtn.setVisible(false);
    }


    /**
     * Extra method for creating and adding handlers to Task
     */
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
        addTaskHandlers(task, false);
    }
    private void addTaskHandlers(Task t, boolean interrupt){
        // Add handlers
        t.setOnFailed(event -> {
            Util.alert(
                Alert.AlertType.ERROR,
                "Some paths are invalid",
                "Please, check all paths",
                ButtonType.OK);
            if (interrupt){
                onCancel(null);
            }
                });

        t.setOnCancelled(
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

    /**
     * Show data in different views
     */
    public void onSaveDataView(ActionEvent actionEvent) {
        if (reducedMatrixDataset.getKeys().isEmpty() || complexityDataset.getKeys().isEmpty()){
            Util.alert(
                    Alert.AlertType.ERROR,
                    "No data to save",
                    "Please, load data first",
                    ButtonType.OK
            );
            return;
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BindedData<TSPReducedMatrix, Complexity> bindedData =
                        new DataBinder<>(reducedMatrixDataset, complexityDataset).bind();
                PrintStream ps = new PrintStream(fSaveData.get());

                switch (saveIndex.get()){
                    case 0:
                        DataView.table(bindedData, ps, ";");

                        break;
                    case 1:
                        DataView.list(bindedData, ps);
                        break;
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> Util.alert(
                Alert.AlertType.INFORMATION,
                "Data saved",
                "Data saved in file " + fSaveData.get().getName(),
                ButtonType.OK
        ));
        new Thread(task).start();
    }



}
