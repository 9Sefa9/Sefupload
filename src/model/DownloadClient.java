package model;

import controller.Controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
/*Diese Klasse, received regelmäßig, die von anderen empfangene Daten*/
public class DownloadClient implements Runnable,Serializable {

    private Controller controller;
    private Socket socket;
    private int id;
    public DownloadClient(Controller controller,int id){
        this.controller = controller;
        this.id = id;
        try{
            socket = new Socket("127.0.0.1",3124);

            System.err.println("CLIENT => SERVER :: LISTENING FOR OWN FOLDER IN SERVER...");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        ObjectInputStream ois=null;
        ObjectOutputStream oos =null;
        ArrayList<String> tmp=null;
        try {

            ois= new ObjectInputStream(this.socket.getInputStream());
            oos = new ObjectOutputStream(this.socket.getOutputStream());

                //sende eigene ID
                oos.writeInt(id);
                oos.flush();
                while(true) {
                    //empfange ArrayList
                    tmp = (ArrayList<String>) ois.readObject();

                    //Formatiere es für ListView
                    ObservableList fileList = FXCollections.observableList(tmp);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.getDownloadList().setItems(fileList.sorted());
                        }
                    });

                    Thread.sleep(5000);
                }
            } catch(IOException|ClassNotFoundException |InterruptedException e){
                System.err.print("SERVER => CLIENT :: Irgend etwas ist schief gelaufen bei der Übertragung!:");
                e.printStackTrace();
            }
    }
}
