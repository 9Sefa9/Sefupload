package model;

import controller.Controller;
import javafx.concurrent.Task;

import java.io.*;
import java.net.URLDecoder;

public class DownloadClient extends Task<Void> {
    private Controller controller;
    private String myObject;
    public DownloadClient(Controller controller, String myObject){
        this.controller= controller;
        this.myObject = myObject;
    }
    @Override
    protected Void call() {
        String path = DownloadClient.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        FileInputStream fos = null;
        ObjectInputStream oos=null;

        try{
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            String fileName = controller.getDownloadList().getItems().get(controller.getDownloadList().getSelectionModel().getSelectedIndex());
            fos = new FileInputStream(decodedPath + fileName);
            oos = new ObjectInputStream(fos);
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
public static void main(String[]args){

}

}
