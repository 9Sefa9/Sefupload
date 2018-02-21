package controller;

import generator.IDGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import model.Model;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Controller {
    private int id;
    private File file;
    private FileChooser fileChooser;
    private Model model;
    private double xOffset = 0, yOffset=0;
    @FXML private Pane pane;
    @FXML private ListView<File> uploadList;
    @FXML protected Label idLabel;
    @FXML private Button deleteButton;

    public Controller(){

        ThreadClient t = new ThreadClient(this);
        t.start();
    }
    @FXML
    public void initialize(){
        model = new Model();
        uploadList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }
    @FXML
    public void deleteButtonData(){

    }
    @FXML
    public void sendButtonData(){

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
    @FXML
    public Label getIdLabel() {
        return idLabel;
    }

    public void setIdLabel(Label idLabel) {
        this.idLabel = idLabel;


    }
}
class ThreadClient extends Thread{
    private Socket client;
    private BufferedWriter bw;
    private BufferedReader br;
    private Controller controller;
    public ThreadClient(Controller controller){
        this.controller = controller;
    }
    @Override
    public void run(){
        try{
            client = new Socket("localhost",3121);
            bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            controller.setId(br.read());
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(br!=null)
                br.close();
                if(bw!=null)
                bw.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}