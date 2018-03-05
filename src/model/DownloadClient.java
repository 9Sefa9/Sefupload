package model;

import controller.Controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class DownloadClient implements Runnable {

    private Controller controller;
    private Socket socket;
    public DownloadClient(Controller controller){
        this.controller = controller;
        try{
            socket = new Socket("localhost",3123);
            System.err.println("CLIENT => SERVER :: LISTENING FOR OWN FOLDER IN SERVER...");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
            try (ObjectInputStream ois = new ObjectInputStream(this.socket.getInputStream())) {
                while(true){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ObservableList tmp=null;
                            try {
                                tmp = (ObservableList)ois.readObject();
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            controller.getDownloadList().setItems(tmp);
                        }
                    });
                    this.socket.setSoTimeout(5000);
                }

            } catch(IOException e){
                e.printStackTrace();
            }
    }
}
