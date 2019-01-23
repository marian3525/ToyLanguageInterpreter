import controller.GUIController;
import controller.SelectionController;
import exceptions.RepositoryException;
import exceptions.SyntaxException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * Start the interpreter in either CLI or GUI mode
     *
     * @param args: <cli>, <gui> or <example> to pick the UI mode
     * @throws SyntaxException
     * @throws RepositoryException
     */
    public static void main(String[] args) {
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
        // build the selector window first as its controller is needed by the main window
        FXMLLoader selectorLoader = new FXMLLoader();
        selectorLoader.setLocation(getClass().getResource("view/gui/selectorUi.fxml"));
        Parent selectorWindow = selectorLoader.load();

        SelectionController selectionController = selectorLoader.getController();
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Selector Window");
        secondaryStage.setScene(new Scene(selectorWindow));
        secondaryStage.show();

        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("view/gui/ui.fxml"));
        Parent mainWindow = mainLoader.load();
        GUIController guiController = mainLoader.getController();
        guiController.setSelectorController(selectionController);

        // the selector window will call setCurrentProgram() on the main one, so it will need a reference back to it
        selectionController.setUiController(guiController);

        primaryStage.setTitle("Toy Language Interpreter");
        primaryStage.setScene(new Scene(mainWindow));
        primaryStage.show();

   }
}