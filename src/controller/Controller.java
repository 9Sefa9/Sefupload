package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import model.Model;

import java.io.File;

public class Controller {
    private File file;
    private FileChooser fileChooser;
    private Model model;
    private double xOffset = 0, yOffset=0;
    @FXML private Pane pane;
    @FXML private ListView<File> uploadList;

    public Controller(){
        model = new Model();
        uploadList = new ListView<>();
        uploadList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }
    @FXML
    public void windowDragged(MouseEvent event){
        Main.getPrimaryStage().setX(event.getScreenX() + xOffset);
        Main.getPrimaryStage().setY(event.getScreenY() + yOffset);
    }
    @FXML
    public void windowPressed(MouseEvent event){
        xOffset = Main.getPrimaryStage().getX() - event.getScreenX();
        yOffset = Main.getPrimaryStage().getY() - event.getScreenY();
    }
    @FXML
    private void chooseDataButton() {
        try {
            //Observable
            this.uploadList.setItems(model.getFileArrayList());

            //Choose one File
            fileChooser = new FileChooser();
            fileChooser.setTitle("Select your file!");
            file = fileChooser.showOpenDialog(new Stage());

            System.out.println("Choosen files location :: "+ file.getAbsoluteFile());
            model.getFileArrayList().add(file);

        }catch(NullPointerException e ){
            System.err.println("No file was added or selected!");
        }
        catch(Exception e ){
            e.printStackTrace();
        }
    }
    @FXML
    public void minimizeWindow(){
        Main.getPrimaryStage().setIconified(true);
    }
    @FXML
    public void closeProgram(){
        Platform.exit();
    }
}
