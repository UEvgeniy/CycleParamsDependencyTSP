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
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.BindedData;
import model.Complexity;
import model.Dataset;
import model.TSPReducedMatrix;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Controller {

    /************************* Fields and Properties *************************/
    private Dataset<TSPReducedMatrix> reducedMatrixDataset;
    private Dataset<Complexity> complexityDataset;
    private Map<ImportDataType, BiFunction<File, Boolean, Task<Dataset<TSPReducedMatrix>>>> importTasks;

    private ObjectProperty<File> fLoadMatrixes, fLoadComplexity, fExportData;
    private IntegerProperty size;

    /********************* UI controllers *********************/
    // Import Data
    @FXML
    public ComboBox<ImportDataType> cbImportData;
    public Button btnBrowseMatrixes, btnBrowseComplexities, btnCancel, btnImport;
    public TextField tfLoadMatrixes, tfLoadComplexities;
    public Label lblFolder, lblFile, lblNumEl;
    public CheckBox cbSubfolders;
    public ProgressBar progressBar;

    // Export Data
    @FXML
    public TitledPane paneExport;
    public ComboBox<ExportDataType> cbExportData;
    public Button btnBrowseExport, btnExport;
    public TextField tfSave;

    // Correlation coefficient
    @FXML
    public TitledPane paneCorrelation;
    public ComboBox<ReducedMatrixFunctional> cbCycleParam;
    public ComboBox<Correlation> cbCorrelation;

    /************************ Initialize **********************/
    public void initialize(){
        initComboboxes();
        initFieldsBindProperties();
        initControllersProperties();
        initImportTasks();
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
        // File paths
        fLoadMatrixes = new SimpleObjectProperty<>();
        fLoadComplexity = new SimpleObjectProperty<>();
        fExportData = new SimpleObjectProperty<>();
        // Number of elements in dataset
        size = new SimpleIntegerProperty(0);

        // Bindings
        tfLoadMatrixes.textProperty().bind(fLoadMatrixes.asString());
        tfLoadComplexities.textProperty().bind(fLoadComplexity.asString());
        tfSave.textProperty().bind(fExportData.asString());

        lblFolder.textProperty().bind(Bindings.format("%s:", cbImportData.getSelectionModel().selectedItemProperty()));
        lblNumEl.textProperty().bind(Bindings.format("%d matrixes loaded", size));
    }

    private void initControllersProperties(){
        // Import
        btnBrowseMatrixes.setOnAction(
                event -> BrowseListeners.browse(cbImportData.getValue(), event, fLoadMatrixes));
        btnBrowseComplexities.setOnAction(
                event -> BrowseListeners.browse(ImportDataType.TXT_COMPLEXITY, event, fLoadComplexity));
        btnCancel.setVisible(false);
        progressBar.setVisible(false);

        // Export
        paneExport.setDisable(true);
        btnBrowseExport.setOnAction(event -> BrowseListeners.browse(cbExportData.getValue(), event, fExportData));

        // Correlation
        paneCorrelation.setDisable(true);
    }

    private void initImportTasks(){

        importTasks = new HashMap<>();
        importTasks.put(ImportDataType.TXT_MATRIXES, TxtMatrixLoader::new);
        importTasks.put(ImportDataType.OBJ_MATRIXES, ObjReducedMatrixesLoader::new);
        importTasks.put(ImportDataType.OBJ_DATASET, ObjReducedDatasetLoader::new);
    }

    /********************** Add listeners *********************/

    /**
     * Load data from paths
     */
    public void onImport(ActionEvent actionEvent) {

        long start = System.currentTimeMillis();
        fLoadMatrixes.getValue();

        // Init task for loading datasets of matrices and complexities
        Task<Dataset<TSPReducedMatrix>> matrixLoad;
        Task<Dataset<Complexity>> compLoad;
        try {
            matrixLoad = importTasks.get(cbImportData.getValue()).apply(fLoadMatrixes.getValue(), cbSubfolders.isSelected());
            compLoad = new TxtComplexityLoader(fLoadComplexity.get());
        }
        catch (NullPointerException e){
            UiUtils.Alerts.errorNullPaths();
            return;
        }

        // Bind progress bar with loading dataset task
        progressBar.progressProperty().bind(matrixLoad.progressProperty());

        // Init task for loading both complexity and dataset
        Task<Datasets<TSPReducedMatrix, Complexity>> commonTask = new Task<Datasets<TSPReducedMatrix, Complexity>>() {
            @Override
            protected Datasets<TSPReducedMatrix, Complexity> call() throws Exception {
                compLoad.run();
                matrixLoad.run();
                return new Datasets<>(matrixLoad.get(), compLoad.get());
            }
        };

        btnCancel.setOnAction(event -> commonTask.cancel());

        // Add listeners for cancelling, failing and succeed task
        addHandlers(commonTask, start);

        // Start loading
        Thread t = new Thread(commonTask);
        t.setDaemon(true);
        t.start();
    }

    private void addHandlers(Task<Datasets<TSPReducedMatrix, Complexity>> task, long start){
        // Cancel
        task.setOnCancelled(event -> UiUtils.Alerts.infoCancel());

        // Fail
        task.setOnFailed(event -> {
            Throwable reason = event.getSource().getException().getCause();
            if (reason instanceof NumberFormatException || reason instanceof ClassNotFoundException){
                UiUtils.Alerts.errorFileInvalid(reason.getMessage());
            }
            else if (reason instanceof IOException){
                UiUtils.Alerts.errorIO(reason.getMessage());
            }
            else {
                UiUtils.Alerts.errorFileInvalid("");
            }
        }); // todo more info

        // Run
        task.setOnRunning(event -> {
            btnImport.disableProperty().bind(task.runningProperty());
            btnCancel.visibleProperty().bind(task.runningProperty());
            progressBar.visibleProperty().bind(task.runningProperty());
        });

        // Completed
        task.setOnSucceeded(event -> {
            if (task.getValue().getFirst() == null || task.getValue().getFirst().size() == 0){
                UiUtils.Alerts.errorNothingToLoad("matrices");
                return;
            }
            reducedMatrixDataset = task.getValue().getFirst();

            if (task.getValue().getSecond() == null || task.getValue().getSecond().size() == 0){
                UiUtils.Alerts.errorNothingToLoad("complexities");
                return;
            }
            complexityDataset = task.getValue().getSecond();

            paneExport.setDisable(false);
            paneCorrelation.setDisable(false);

            size.setValue(reducedMatrixDataset.getKeys().size());
            UiUtils.Alerts.infoSuccessImport(task.getValue().getFirst().size(), (double)(System.currentTimeMillis() - start) / 1000); });
    }

    /**
     * Show data in different views
     */
    public void onExport(ActionEvent actionEvent) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BindedData<TSPReducedMatrix, Complexity> bindedData =
                        new DataBinder<>(reducedMatrixDataset, complexityDataset).bind();
                PrintStream stream = null;

                if (!fExportData.get().isDirectory()){
                    stream = new PrintStream(fExportData.get());
                }

                switch (cbExportData.getValue()){
                    case DISTR_CYCLE_LEN:
                        DataView.distrCyclesLen(bindedData, stream, ";");
                        break;
                    case LIST:
                        DataView.list(bindedData, stream);
                        break;
                    case SERIALIZE_MATRIXES:
                        new ObjReducedMatrixesSaver(reducedMatrixDataset, fExportData.get()).save();
                        break;
                    case SERIALIZE_DATASET:
                        new ObjReducedDatasetSaver(reducedMatrixDataset, fExportData.get()).save();
                        break;
                    case TABLE_FUNCTIONS:
                        DataView.dot_diagram(bindedData, stream);
                        break;
                    case DISTR_CYCLE_TO_CITY:
                        DataView.distrCyclesToCities(bindedData, stream, ";");
                        break;
                }
                if (stream != null) {
                    stream.close();
                }
                return null; }};

        addHandlers(task);
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void addHandlers(Task<Void> task){
        // Failed
        task.setOnFailed(event -> {
            Throwable reason = event.getSource().getException();
            if (reason instanceof NullPointerException){
                UiUtils.Alerts.errorNullPaths();
            }
            else if (reason instanceof  IOException){
                UiUtils.Alerts.errorIO(reason.getMessage());
            }
            else{
                UiUtils.Alerts.errorFileInvalid("");
            }
        });

        task.setOnRunning(event -> {
            btnExport.disableProperty().bind(task.runningProperty());
        });

        // Succeed
        task.setOnSucceeded(event -> {
            if (Desktop.isDesktopSupported()){
                try {
                    Desktop.getDesktop().open(
                            fExportData.get().isDirectory() ?
                                    fExportData.get() :
                                    fExportData.get().getParentFile()
                    );
                } catch (IOException e) {
                }
            }
            UiUtils.Alerts.infoSuccessExport(fExportData.get());
        });
    }

    /**
     * Count correlation coefficient
     */
    public void onExperiment(ActionEvent actionEvent) {

        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() {
                BindedData<TSPReducedMatrix, Complexity> bindedData =
                        new DataBinder<>(reducedMatrixDataset, complexityDataset).bind();

                List<Double> paramsOfReducedMatrix =
                        TSPConverter.toParamsDataset(bindedData.getFirst(), cbCycleParam.getValue());

                List<Double> lComplexities =
                        TSPConverter.toDouble(bindedData.getSecond());

                return cbCorrelation.getValue().count(paramsOfReducedMatrix, lComplexities);
            }
        };

        task.setOnSucceeded(event -> UiUtils.Alerts.infoSuccessExperiment(task.getValue())
        );

        new Thread(task).start();
    }
}
