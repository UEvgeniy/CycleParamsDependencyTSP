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
import model.BindedData;
import model.Complexity;
import model.Dataset;
import model.TSPReducedMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Controller {

    /************************* Fields *************************/
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
    public Button btnBrowseExport;
    public TextField tfSave;

    // Correlation coefficient
    @FXML
    public TitledPane paneCorrelation;
    public ComboBox<ReducedMatrixFunctional> cbCycleParam;
    public ComboBox<Correlation> cbCorrelation;

    /*********************** Properties ***********************/

    private ObjectProperty<File> fLoadMatrixes, fLoadComplexity, fExportData;
    private BooleanProperty subfolders;
    private IntegerProperty size;
    private Map<ImportDataType, BiFunction<File, Boolean, Dataset<TSPReducedMatrix>>>  importTasks;
    private Map<ImportDataType, DatasetLoader<TSPReducedMatrix>> xxx;
    Task<Dataset<TSPReducedMatrix>> currentTask;

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
        reducedMatrixDataset = new Dataset<>();
        complexityDataset = new Dataset<>();

        fLoadMatrixes = new SimpleObjectProperty<>();

        fExportData = new SimpleObjectProperty<>();
        fLoadComplexity = new SimpleObjectProperty<>();
        subfolders = new SimpleBooleanProperty();



        size = new SimpleIntegerProperty(0);


        tfLoadMatrixes.textProperty().bind(fLoadMatrixes.asString());
        tfLoadComplexities.textProperty().bind(fLoadComplexity.asString());
        tfSave.textProperty().bind(fExportData.asString());
        subfolders.bind(cbSubfolders.selectedProperty());

        lblFolder.textProperty().bind(
                Bindings.format("%s:", cbImportData.getSelectionModel().selectedItemProperty())
        );
        lblNumEl.textProperty().bind(
                Bindings.format("%d matrixes loaded", size)
        );
    }

    private void initControllersProperties(){
        // Import
        btnBrowseMatrixes.setOnAction(ImportCore.onBrowseMatrix(cbImportData.getValue(), fLoadMatrixes));
        btnBrowseComplexities.setOnAction(ImportCore.onBrowseComplexity(fLoadComplexity));

        btnCancel.setVisible(false);
        btnCancel.setOnAction(this::onCancel);

        // Export
        paneExport.setDisable(true);
        btnBrowseExport.setOnAction(ExportCore.onBrowseExport(cbExportData.getValue(), fExportData));

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
                UiUtils.Alerts.infoSuccessExperiment(task.getValue())
        );

        new Thread(task).start();
    }

    /**
     * Load data from paths
     */
    public void onLoadData(ActionEvent actionEvent) {
        long start = System.currentTimeMillis();

        initLoadComplexity();

 /*       Task<Void> task = new Task<Void>() {
            @Override
            protected void call() throws Exception {

//                complexityDataset = new TxtComplexityLoader(fLoadComplexity.get()).load();
//                reducedMatrixDataset = importTasks.get(cbImportData.getValue())
//                        .apply(fLoadMatrixes.get(), subfolders.get()).getValue();
            }
        };*/

        //currentTask = importTasks.get(cbImportData.getValue())
        //        .apply(fLoadMatrixes.get(), subfolders.get());

        btnCancel.visibleProperty().bind(currentTask.runningProperty());
        progressBar.progressProperty().bind(currentTask.progressProperty());

        currentTask.setOnSucceeded(event -> {
            reducedMatrixDataset = currentTask.getValue();
            if (reducedMatrixDataset == null || reducedMatrixDataset.size() == 0 ||
                    complexityDataset == null || complexityDataset.size() == 0){
                UiUtils.Alerts.errorNothingToLoad();
                return;
            }
            paneExport.setDisable(false);
            paneCorrelation.setDisable(false);

            size.setValue(reducedMatrixDataset.getKeys().size());
            UiUtils.Alerts.infoSuccessImport(currentTask.getValue().size(), 42.); // todo
        });


        currentTask.setOnCancelled( event ->{
            UiUtils.Alerts.infoCancel();
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
        });

        currentTask.setOnFailed(event -> {
            UiUtils.Alerts.errorNullPaths();
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
        }); // todo change error msg

        Thread t = new Thread(currentTask);
        t.setDaemon(true);
        t.start();
    }

    private void initLoadComplexity(){
        Task<Dataset<Complexity>> complLoader = new TxtComplexityLoader(fLoadComplexity.get());

        // todo catch illegalArgException

        // todo make in sep method
        complLoader.setOnCancelled( event ->
                UiUtils.Alerts.infoCancel());

        complLoader.setOnFailed(event ->
                UiUtils.Alerts.errorNullPaths());

        complLoader.setOnSucceeded(event -> {
            complexityDataset = complLoader.getValue(); });

        new Thread(complLoader).start();
    }


    // Cancel
    private void onCancel(ActionEvent actionEvent){
        if (currentTask != null && currentTask.isRunning())
            currentTask.cancel();
    }

    /**
     * Extra method for creating and adding handlers to Task
     */
    private void initImportTasks(){
        importTasks = new HashMap<>();
        importTasks.put(ImportDataType.TXT_MATRIXES, ((file, aBoolean) ->
                {
                    Dataset<TSPReducedMatrix> res = null;
                    try {
                        res = TSPConverter.toReducedDataset(new TxtMatrixLoader(file, aBoolean).load(),
                                ReducingOrder.RowsColumns);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return res;
                }));
        //importTasks.put(ImportDataType.OBJ_MATRIXES,  ObjReducedMatrixesLoader);
        //importTasks.put(ImportDataType.OBJ_DATASET, ObjReducedDatasetLoader::new);

        xxx = new HashMap<>();
        //xxx.put(ImportDataType.TXT_MATRIXES, TxtMatrixLoader::new);
    }
    private void addTaskHandlers(Task t, boolean interrupt){
        // Add handlers
        t.setOnFailed(event -> {
            UiUtils.Alerts.errorInvalidPath();
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
                    UiUtils.Alerts.infoCancel();
                }
        );
    }

    /**
     * Show data in different views
     */
    public void onSaveDataView(ActionEvent actionEvent) {
        if (reducedMatrixDataset.getKeys().isEmpty() || complexityDataset.getKeys().isEmpty()){
            UiUtils.Alerts.errorNoData();
            return;
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BindedData<TSPReducedMatrix, Complexity> bindedData =
                        new DataBinder<>(reducedMatrixDataset, complexityDataset).bind();

                PrintStream stream;
                switch (cbExportData.getValue()){
                    case DISTR_CYCLE_LEN:
                        stream = new PrintStream(fExportData.get());
                        DataView.distrCyclesLen(bindedData, new PrintStream(fExportData.get()), ";");
                        stream.close();
                        break;
                    case LIST:
                        stream = new PrintStream(fExportData.get());
                        DataView.list(bindedData, new PrintStream(fExportData.get()));
                        stream.close();
                        break;
                    case SERIALIZE_MATRIXES:
                        new ObjReducedMatrixesSaver(reducedMatrixDataset, fExportData.get()).save();
                        break;
                    case SERIALIZE_DATASET:
                        new ObjReducedDatasetSaver(reducedMatrixDataset, fExportData.get()).save();
                        break;
                    case TABLE_FUNCTIONS:
                        stream = new PrintStream(fExportData.get());
                        DataView.dot_diagram(bindedData, new PrintStream(fExportData.get()));
                        stream.close();
                        break;
                    case DISTR_CYCLE_TO_CITY:
                        stream = new PrintStream(fExportData.get());
                        DataView.distrCyclesToCities(bindedData, new PrintStream(fExportData.get()), ";");
                        stream.close();
                        break;
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> UiUtils.Alerts.infoSuccessExport(fExportData.get()));
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}
