package controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import model.Model;
import model.UploadClient;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;

public class Controller {
    private int id;
    private List<File> fileList;
    private FileChooser fileChooser;
    private Model model;
    private double xOffset = 0, yOffset=0;
    @FXML private Pane pane;
    @FXML private ListView<File> uploadList;
    @FXML protected Label idLabel;
    @FXML private Button deleteButton;
    @FXML private TextField textFieldID;
    @FXML private ProgressBar sendBar;

    @FXML
    public void initialize(){
        model = new Model();
        uploadList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ThreadClientID t = new ThreadClientID(this);
        t.start();
    }

    @FXML
    public void deleteDataButton(){
        if(!this.model.getFileArrayList().isEmpty()) {
            ObservableList<File> selectedFiles = this.uploadList.getSelectionModel().getSelectedItems();
            System.err.println("DELETE FROM LIST :: " + selectedFiles);
            this.model.getFileArrayList().removeAll(selectedFiles);
        }
    }
    @FXML
    public void sendDataButton() throws IOException {

        if(!this.model.getFileArrayList().isEmpty() && this.id>0 && (!this.textFieldID.getText().isEmpty() && this.textFieldID.getText().matches("[0-9]+") && this.textFieldID.getText().length() > 3)){
            Task<Void> task = new UploadClient(this.model, this);
            this.sendBar.progressProperty().bind(task.progressProperty());
            Thread thread = new Thread(task);
            thread.start();
        }
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
            fileChooser.setTitle("SELECT YOUR FILES!");
            fileList = fileChooser.showOpenMultipleDialog(new Stage());

            model.getFileArrayList().addAll(fileList);

        }catch(NullPointerException e ){
            System.err.println("No fileList was added or selected!");
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
    public Label getIdLabel() {
        return this.idLabel;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public TextField getTextFieldID() {
        return textFieldID;
    }

    public void setTextFieldID(TextField textFieldID) {
        this.textFieldID = textFieldID;
    }
}



class ThreadClientID extends Thread{
    private Socket client;
    private BufferedWriter bw;
    private BufferedReader br;
    private Controller controller;
    public ThreadClientID(Controller controller){
        this.controller = controller;
    }
    @Override
    public void run(){
        try{
            client = new Socket("localhost",3121);
            bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.err.println("CLIENT => SERVER :: GET ID FROM DATABASE ..."+ client.getInetAddress().getHostName());

            bw.write("requestID"+"\n");
            bw.flush();

            bw.write(client.getInetAddress().getHostName()+"\n");
            bw.flush();

            int newID = br.read();
            System.err.println("CLIENT => SERVER :: ID RETRIVED..."+ client.getInetAddress().getHostName() +" ID: "+newID);
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    controller.setId(newID);
                    controller.getIdLabel().setText("Deine ID:"+newID);
                }
            });


        }catch(ConnectException c){
            System.err.println("Server connection refused!");
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    controller.getIdLabel().setText("Keine Internetverbindung!");
                }
            });
        }catch(IOException e){e.printStackTrace();}
        finally{
            try{
                if(br!=null)
                br.close();
                if(client!=null)
                client.close();
                if(bw!=null)
                bw.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}