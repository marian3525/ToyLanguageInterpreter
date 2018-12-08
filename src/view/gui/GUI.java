package view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.UI;

public class GUI extends Application implements UI {

    public void run() {
        System.out.println("Started GUI");
        launch();
    }

    /**
     * Start the GUI
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("config.fxml"));
        primaryStage.setTitle("Hello Word");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

}
