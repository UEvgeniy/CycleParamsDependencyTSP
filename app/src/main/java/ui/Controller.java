package ui;

import com.sun.javafx.collections.ObservableListWrapper;
import control.*;
import data_view.DataView;
import io.*;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Pair;
import model.BindedData;
import model.Complexity;
import model.Dataset;
import model.TSPReducedMatrix;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class Controller {
    private static final String INITIAL_PATH = "D://VKR/data";

    public void onExperiment(ActionEvent actionEvent) {
        Task<Double> task = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                BindedData<TSPReducedMatrix, Complexity> bindedData =
                        new DataBinder<>(reducedMatrixDataset, complexityDataset).bind();

                List<Double> paramsOfReducedMatrix =
                        TSPConverter.toParamsDataset(
                                bindedData.getFirst(),
                                cbCycleParam.getValue().getKey());

                List<Double> lComplexities =
                        TSPConverter.toDouble(bindedData.getSecond());

                return cbCorrelation.getValue().count(paramsOfReducedMatrix, lComplexities);
            }
        };

        task.setOnSucceeded(event ->
                Util.alert(
                        Alert.AlertType.INFORMATION,
                        "Experiments compeleted",
                        String.format("The result correlation is %1$,.2f", task.getValue()),
                        ButtonType.OK)
        );

        new Thread(task).start();
    }


    /**
     *  Class Util with helping methods
     */
    static class Util{
        static void alert(Alert.AlertType type, String header, String text, ButtonType... buttons){
            Alert al = new Alert(type, text, buttons);
            al.setHeaderText(header);
            al.showAndWait();
        }
        static FileChooser.ExtensionFilter initFilter(String desc, String... ext){
            return new FileChooser.ExtensionFilter(desc, ext);
        }
    }

    class ParamsPair extends Pair<ReducedMatrixParameter, String>{
        public ParamsPair(ReducedMatrixParameter key, String value) {
            super(key, value);
        }
        @Override
        public String toString() {
            return getValue();
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
    public ComboBox<String> cbLoadedData, cbTypeView;
    public ComboBox<ParamsPair> cbCycleParam;
    public ComboBox<Correlation> cbCorrelation;
    @FXML
    public Button btnBrMatr, btnBrSave, btnBrCompl, btnLoad, cancelBtn;
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
    // todo refactor
    public void initialize(){
        // Comboboxes
        cbLoadedData.getItems().addAll(
                "Txt matrixes files", "Obj matrixes files", "Obj dataset file");
        //cbCorrelation.getItems().addAll("Pearson", "Spearman");
        //cbCycleParam.getItems().addAll("Cycle length", "Number of cycles");
        cbTypeView.getItems().addAll("Table", "List", "Serialize matrixes", "Serialize dataset");

        cbCorrelation.setItems(FXCollections.observableArrayList(new PearsonCorrelation(),
                new SpearmanCorrelation()));

        ObservableList<ParamsPair> list = FXCollections.observableArrayList(
                new ParamsPair(Parameters::cyclesNum, "Number of cycles"),
                new ParamsPair(Parameters::uniqueCyclesNum, "Number of unique cycles"),
                new ParamsPair(Parameters::sumCycleLength, "Sum of cycle length"),
                new ParamsPair(Parameters::averageCycleLength, "Average Cycle Length"),
                new ParamsPair(Parameters::maxCycleLength, "Max cycle length"),
                new ParamsPair(Parameters::avgMultipleMaxLenght, "Average * Max")
                //Parameters::cyclesNum, Parameters::uniqueCyclesNum, Parameters::sumCycleLength,
                //Parameters::averageCycleLength, Parameters::maxCycleLength
        );
        //Pair<ReducedMatrixParameter, String> as = new Pair<>()
        //ReducedMatrixParameter as = Parameters::cyclesNum;
        //cbCycleParam = new ComboBox<>(list);
        //cbCycleParam.getSelectionModel().selectFirst();
        cbCycleParam.setItems(list);
        /*cbCycleParam.setCellFactory(new Callback<ListView<String>,ListCell<String>>(){

            @Override
            public ListCell<String> call(ListView<String> p) {

                return new ListCell<String>(){
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);

                        if(t != null){
                            setText(t + ":" + t);
                        }else{
                            setText(null);
                        }
                    }
                };
            }
        });*/

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
    public void onBrowse(ActionEvent actionEvent) {

        Window win = ((Node) actionEvent.getSource()).getScene().getWindow();
        Button sourse = (Button) actionEvent.getSource();
        FileChooser.ExtensionFilter txt = Util.initFilter("TXT file", "*.txt");
        FileChooser.ExtensionFilter csv = Util.initFilter("CSV file", "*.csv");
        FileChooser.ExtensionFilter ds = Util.initFilter("Dataset file", "*.ds");

        if (sourse == btnBrMatr){
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
        else if(sourse == btnBrCompl){
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
            }
        }
    }
    private void browseFile(FileChooser.ExtensionFilter filter, ObjectProperty<File> file,
                            Window win, boolean isOpen){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(INITIAL_PATH));
        fileChooser.getExtensionFilters().add(filter);

        file.setValue(isOpen ?
                fileChooser.showOpenDialog(win) :
                fileChooser.showSaveDialog(win));
    }
    private void browseDir(ObjectProperty<File> file, Window win){
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(new File(INITIAL_PATH));
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
            progressBar.progressProperty().unbind();
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
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
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
                    case 2:
                        new ObjReducedMatrixesSaver(reducedMatrixDataset, fSaveData.get()).save();
                        break;
                    case 3:
                        new ObjReducedDatasetSaver(reducedMatrixDataset, fSaveData.get()).save();
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
