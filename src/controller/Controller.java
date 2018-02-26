package controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import model.Model;
import model.Downloader;
import model.Uploader;

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

    @FXML
    public void initialize(){
        model = new Model();
        uploadList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ThreadClient t = new ThreadClient(this);
        t.start();
    }

    public void setId(int id){
        this.id = id;
    }
    @FXML
    public void deleteDataButton(){
        ObservableList<File> selectedFiles = this.uploadList.getSelectionModel().getSelectedItems();
        System.err.println("DELETE FROM LIST :: "+selectedFiles);
        this.model.getFileArrayList().removeAll(selectedFiles);
    }
    @FXML
    public void sendDataButton() throws IOException {
        Uploader upload= new Uploader();
        upload.start();
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
            System.out.println("CLIENT => SERVER :: "+ client.getInetAddress().getHostName());

            bw.write("requestID"+"\n");
            bw.flush();

            bw.write(client.getInetAddress().getHostName()+"\n");
            bw.flush();

            int newID = br.read();
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
                br.close();
                client.close();
                bw.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}