package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    private static Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/GUI.fxml"));
        Scene scene = new Scene(root,450,600);
        scene.getStylesheets().add(getClass().getResource("/css/ListView.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest( event -> {
            final ExecutorService exec = Executors.newCachedThreadPool();
            exec.shutdown();
            System.out.println("BACKGROUND TASKS WERE EXECUTED.\nPROGRAM CLOSED PROPERLY.");
        }
        );
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

}
