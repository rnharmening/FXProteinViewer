package main;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PDBViewer extends Application{

    private static Stage primaryStage;

    @Override
    public void init() throws Exception {

        System.out.println("java version: "+System.getProperty("java.version"));
        System.out.println("javafx.version: " + System.getProperty("javafx.version"));
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


//        AnchorPane rootPane = new AnchorPane();
//        Controller controller = new Controller();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout.fxml"));
//        fxmlLoader.setRoot(rootPane);
//        fxmlLoader.setController(controller);
        fxmlLoader.setControllerFactory(c -> new Controller());

        Parent root = fxmlLoader.load();
        root.getStylesheets().add("general.css");

        PDBViewer.primaryStage = primaryStage;

        Scene scene = new Scene(root, 1300, 700);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

}
