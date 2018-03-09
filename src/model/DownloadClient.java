package model;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
        try {
            if(this.socket==null)
                System.out.println("test");
            ois= new ObjectInputStream(this.socket.getInputStream());
            oos = new ObjectOutputStream(this.socket.getOutputStream());

                oos.writeInt(id);
                oos.flush();

                ArrayList<File> tmp=null;


                tmp = (ArrayList<File>)ois.readObject();
                for(int i = 0; i<tmp.size();i++){
                    System.out.println(tmp.get(i));
                }

                ObservableList fileList = FXCollections.observableList(tmp);
                controller.getDownloadList().setItems(fileList);

            } catch(IOException|ClassNotFoundException e){
                e.printStackTrace();
            }finally {
            try {
                if (ois != null)
                    ois.close();
                if(oos!=null){
                    oos.close();
                }
            }catch (Exception e ){
                e.printStackTrace();
            }
        }
    }
}
