package view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.UI;

public class SelectorUI extends Application implements UI {

    public void run() {
        //launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("selectorUi.fxml"));
        stage.setTitle("Select the program to run");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
