package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class Controller {

    // Combo Boxes
    @FXML
    public ComboBox cbLoadedData, cbTypeView, cbCycleParam, cbCorrelation;

    @FXML
    public void initialize(){
        // Comboboxes
        cbLoadedData.getItems().addAll(
                "Txt matrixes files", "Obj matrixes files", "Obj dataset file");
        cbCorrelation.getItems().addAll("Pearson", "Spearman");
        cbCycleParam.getItems().addAll("Cycle length", "Number of cycles");
        cbTypeView.getItems().addAll("Table", "List");

    }

    public void onBrowse(ActionEvent actionEvent) {
    }
}
