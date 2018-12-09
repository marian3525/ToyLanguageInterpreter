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
        Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));
        primaryStage.setTitle("Toy Language Interpreter");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
