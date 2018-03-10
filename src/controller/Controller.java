package controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import model.DownloadClient;
import model.RefreshClient;
import model.Model;
import model.UploadClient;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;

//TODO Button events von accept unr reject erstellen. Progres Bar mit Task für XLoad erstellen
public class Controller implements Serializable {
    private int id;
    private List<File> fileList;
    private FileChooser fileChooser;
    private Model model;
    private double xOffset = 0, yOffset=0;
    private Button accept,reject;
    @FXML private Pane pane;
    @FXML private ListView<File> uploadList;
    @FXML private ListView<String> downloadList;
    @FXML protected Label idLabel;
    @FXML private Button deleteButton;
    @FXML private TextField textFieldID;
    @FXML private ProgressBar sendBar;


    @FXML
    public void initialize(){
        model = new Model();
        accept = new Button("Accept");
        reject = new Button("Reject");
        accept.getStylesheets().add("/css/Button.css");
        reject.getStylesheets().add("/css/Button.css");
        uploadList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ThreadClientID t = new ThreadClientID(this);
        t.start();

        this.getDownloadList().setCellFactory(c -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String myObject, boolean b) {
                    super.updateItem(myObject, myObject == null || b);
                    if (myObject != null) {
                        HBox hbox = new HBox();
                        hbox.getChildren().addAll(getAccept(),getReject());
                        setGraphic(hbox);
                        setText(myObject);
                    } else {
                        // wichtig da sonst der text stehen bleibt!
                        setGraphic(null);
                        setText("");
                    }
                }
            };
            return cell;
        });

        /*this.accept.setOnAction(e->{
            DownloadClient dc = new DownloadClient(this);
            dc.start();
        });*/
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
        System.exit(0);
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

    public synchronized ListView<String> getDownloadList() {
        return downloadList;
    }

    public Button getAccept() {
        return this.accept;
    }
    public Button getReject() {
        return this.reject;
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

                    Thread t2 = new Thread(new RefreshClient(controller,newID));
                    t2.start();
                }
            });


        }catch(ConnectException c){
            System.err.print("CLIENT => SERVER :: Irgend etwas ist schief gelaufen bei der ID Übertragung!...:");
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