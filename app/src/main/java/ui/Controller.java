package ui;

import control.*;
import control.functionals.*;
import data_view.DataView;
import io.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import model.BindedData;
import model.Complexity;
import model.Dataset;
import model.TSPReducedMatrix;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class Controller {

    /************************* Fields *************************/
    private static final String INITIAL_BROWSE_PATH = "C://";
    private Dataset<TSPReducedMatrix> reducedMatrixDataset;
    private Dataset<Complexity> complexityDataset;

    /********************* UI controllers *********************/
    // Import Data
    @FXML
    public ComboBox<ImportDataType> cbImportData;
    public Button btnBrowseMatrixes, btnBrowseComplexities, btnImport, btnCancel;
    public TextField tfLoadMatrixes, tfLoadComplexities;
    public Label lblFolder, lblFile, lblNumEl;
    public CheckBox cbSubfolders;
    public ProgressBar progressBar;

    // Export Data
    @FXML
    public TitledPane paneExport;
    public ComboBox<ExportDataType> cbExportData;
    public Button  btnBrSave;
    public TextField tfSave;

    // Correlation coefficient
    @FXML
    public TitledPane paneCorrelation;
    public ComboBox<ReducedMatrixFunctional> cbCycleParam;
    public ComboBox<Correlation> cbCorrelation;

    /*********************** Properties ***********************/

    private ObjectProperty<File> fLoadMatrixes, fLoadComplexity, fSaveData;
    private BooleanProperty subfolders;
    private IntegerProperty loadIndex, saveIndex, size;
    private Task<Dataset<TSPReducedMatrix>> task;


    /************************ Initialize **********************/
    public void initialize(){
        initComboboxes();
        initFieldsBindProperties();
        initControllersProperties();
    }

    private void initComboboxes(){
        // Combo boxes
        cbImportData.setItems(FXCollections.observableArrayList(
                ImportDataType.TXT_MATRIXES,
                ImportDataType.OBJ_MATRIXES,
                ImportDataType.OBJ_DATASET)
        );

        cbExportData.setItems(FXCollections.observableArrayList(
                ExportDataType.DISTR_CYCLE_LEN,
                ExportDataType.DISTR_CYCLE_TO_CITY,
                ExportDataType.SERIALIZE_MATRIXES,
                ExportDataType.SERIALIZE_DATASET,
                ExportDataType.LIST,
                ExportDataType.TABLE_FUNCTIONS
        ));

        cbCycleParam.setItems(FXCollections.observableArrayList(
                new NumberOfCycles(),
                new NumberOfUniqueCycles(),
                new MaxCycleLength(),
                new MinCycleLength(),
                new AvgCycleLength(),
                new CycleLenDeviation(),
                new TotalCycleLength(),
                new AvgNumCyclesThroughCity()));

        cbCorrelation.setItems(FXCollections.observableArrayList(
                new PearsonCorrelation(),
                new SpearmanCorrelation()));

        for (ComboBox cb : new ComboBox[]{cbImportData, cbExportData, cbCycleParam, cbCorrelation}){
            cb.getSelectionModel().selectFirst();
        }
    }

    private void initFieldsBindProperties(){
        reducedMatrixDataset = new Dataset<>();
        complexityDataset = new Dataset<>();

        fLoadMatrixes = new SimpleObjectProperty<>();
        fSaveData = new SimpleObjectProperty<>();
        fLoadComplexity = new SimpleObjectProperty<>();
        subfolders = new SimpleBooleanProperty();
        loadIndex = new SimpleIntegerProperty();
        saveIndex = new SimpleIntegerProperty();

        size = new SimpleIntegerProperty(0);

        tfLoadMatrixes.textProperty().bind(fLoadMatrixes.asString());
        tfLoadComplexities.textProperty().bind(fLoadComplexity.asString());
        tfSave.textProperty().bind(fSaveData.asString());
        subfolders.bind(cbSubfolders.selectedProperty());
        loadIndex.bind(cbImportData.getSelectionModel().selectedIndexProperty());
        saveIndex.bind(cbExportData.getSelectionModel().selectedIndexProperty());
        lblFolder.textProperty().bind(
                Bindings.format("%s:", cbImportData.getSelectionModel().selectedItemProperty())
        );
        lblNumEl.textProperty().bind(
                Bindings.format("%d matrixes loaded", size)
        );
    }

    private void initControllersProperties(){
        // Import
        btnCancel.setVisible(false);
        btnCancel.setOnAction(ImportCore.onCancel(task));

        // Export
        paneExport.setDisable(true);

        // Correlation
        paneCorrelation.setDisable(true);

    }
    /********************** Add listeners *********************/

    public void onExperiment(ActionEvent actionEvent) {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() {
                BindedData<TSPReducedMatrix, Complexity> bindedData =
                        new DataBinder<>(reducedMatrixDataset, complexityDataset).bind();

                List<Double> paramsOfReducedMatrix =
                        TSPConverter.toParamsDataset(
                                bindedData.getFirst(),
                                cbCycleParam.getValue());

                List<Double> lComplexities =
                        TSPConverter.toDouble(bindedData.getSecond());

                return cbCorrelation.getValue().count(paramsOfReducedMatrix, lComplexities);
            }
        };

        task.setOnSucceeded(event ->
                UiUtils.alert(
                        Alert.AlertType.INFORMATION,
                        "Experiments compeleted",
                        String.format("The result correlation is %1$,.2f", task.getValue()),
                        ButtonType.OK)
        );

        new Thread(task).start();
    }

    /**
     * Browse button
     */
    public void onBrowse(ActionEvent actionEvent) {

        Window win = ((Node) actionEvent.getSource()).getScene().getWindow();
        Button sourse = (Button) actionEvent.getSource();
        FileChooser.ExtensionFilter txt = UiUtils.initFilter("TXT file", "*.txt");
        FileChooser.ExtensionFilter csv = UiUtils.initFilter("CSV file", "*.csv");
        FileChooser.ExtensionFilter ds = UiUtils.initFilter("Dataset file", "*.ds");

        if (sourse == btnBrowseMatrixes){
            switch (loadIndex.get()){
                case 0:
                    browseDir(fLoadMatrixes, win);
                    break;
                case 1:
                    browseDir(fLoadMatrixes, win);
                    break;
                case 2:
                    browseFile(ds, fLoadMatrixes, win, true);
                    break;
            }
        }
        else if(sourse == btnBrowseComplexities){
            browseFile(txt, fLoadComplexity, win, true);
        }
        else if (sourse == btnBrSave){
            switch (saveIndex.get()){
                case 0:
                    browseFile(csv, fSaveData, win, false);
                    break;
                case 1:
                    browseFile(txt, fSaveData, win, false);
                    break;
                case 2:
                    browseDir(fSaveData, win);
                    break;
                case 3:
                    browseFile(ds, fSaveData, win, false);
                    break;
                case 4:
                    browseFile(csv, fSaveData, win, false);
                    break;
                case 5:
                    browseFile(csv, fSaveData, win, false);
            }
        }
    }
    private void browseFile(FileChooser.ExtensionFilter filter, ObjectProperty<File> file,
                            Window win, boolean isOpen){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(INITIAL_BROWSE_PATH));
        fileChooser.getExtensionFilters().add(filter);

        file.setValue(isOpen ?
                fileChooser.showOpenDialog(win) :
                fileChooser.showSaveDialog(win));
    }
    private void browseDir(ObjectProperty<File> file, Window win){
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(new File(INITIAL_BROWSE_PATH));
        file.setValue(dirChooser.showDialog(win));
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
        complLoader.setOnSucceeded(event -> complexityDataset = complLoader.getValue());
        new Thread(complLoader).start();

        // Inialize tasks
        try {
            initTask();
        }
        catch (NullPointerException e){
            UiUtils.alert(
                    Alert.AlertType.ERROR,
                    "Some paths are null",
                    "Please, fill all the paths",
                    ButtonType.OK);
            progressBar.progressProperty().unbind();
            return;
        }

        // Bind task to progress bar
        progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded((e) -> {
            btnCancel.setVisible(false);
            reducedMatrixDataset = task.getValue();

            if (reducedMatrixDataset == null || reducedMatrixDataset.size() == 0){
                UiUtils.alert(Alert.AlertType.ERROR, "No data to load", "...", ButtonType.OK);
                return;
            }

            paneExport.setDisable(false);
            paneCorrelation.setDisable(false);

            size.setValue(reducedMatrixDataset.getKeys().size());
            UiUtils.alert(
                    Alert.AlertType.INFORMATION,
                    "Datasets successfully loaded",
                    String.format("%d elements loaded for %2$,.2f sec",
                            reducedMatrixDataset.getKeys().size(),
                            (double)(System.currentTimeMillis() - start) / 1000),
                    ButtonType.OK);
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
        btnCancel.setVisible(true);
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
            UiUtils.alert(
                Alert.AlertType.ERROR,
                "Some paths are invalid",
                "Please, check all paths",
                ButtonType.OK);
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            if (interrupt){
                btnCancel.fire();
            }
                });

        t.setOnCancelled(
                event -> {
                    btnCancel.setVisible(false);
                    progressBar.progressProperty().unbind();
                    progressBar.setProgress(0);
                    UiUtils.alert(
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
            UiUtils.alert(
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

                PrintStream stream;
                switch (saveIndex.get()){
                    case 0:
                        stream = new PrintStream(fSaveData.get());
                        DataView.distrCyclesLen(bindedData, new PrintStream(fSaveData.get()), ";");
                        stream.close();
                        break;
                    case 1:
                        stream = new PrintStream(fSaveData.get());
                        DataView.list(bindedData, new PrintStream(fSaveData.get()));
                        stream.close();
                        break;
                    case 2:
                        new ObjReducedMatrixesSaver(reducedMatrixDataset, fSaveData.get()).save();
                        break;
                    case 3:
                        new ObjReducedDatasetSaver(reducedMatrixDataset, fSaveData.get()).save();
                        break;
                    case 4:
                        stream = new PrintStream(fSaveData.get());
                        DataView.dot_diagram(bindedData, new PrintStream(fSaveData.get()));
                        stream.close();
                        break;
                    case 5:
                        stream = new PrintStream(fSaveData.get());
                        DataView.distrCyclesToCities(bindedData, new PrintStream(fSaveData.get()), ";");
                        stream.close();
                        break;
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> UiUtils.alert(
                Alert.AlertType.INFORMATION,
                "Data saved",
                "Data saved in file " + fSaveData.get().getName(),
                ButtonType.OK
        ));
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}
