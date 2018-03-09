package model;

import controller.Controller;

import java.io.*;
import java.net.URLDecoder;

public class DownloadClient extends Thread {
    private Controller controller;
    public DownloadClient(Controller controller){
        this.controller= controller;
    }
    @Override
    public void run() {
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
    }
public static void main(String[]args){

}
}
