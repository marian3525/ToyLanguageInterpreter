package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class SelectionController implements Initializable {
    @FXML
    private ListView<String> programsList;
    private GUIController uiController;

    public void setUiController(GUIController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        programsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        programsList.setOnMouseClicked(mouseEvent -> {
            // notify the UI of the change
            String line = programsList.getSelectionModel().getSelectedItem();
            if(line != null) {
                uiController.setCurrentProgram(programsList.getSelectionModel().getSelectedItem().split(":")[0]);
            }

        });
    }

    void setPrograms(Set<String> states) {

        programsList.setItems(FXCollections.observableArrayList(states));
    }
}
