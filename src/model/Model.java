package model;

import javafx.collections.FXCollections;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Model{
    //fileArrayList
    private ObservableList<File> fileArrayList = FXCollections.observableArrayList();

    public synchronized ObservableList<File> getFileArrayList() {
        return fileArrayList;
    }


    public void sendFiles(Socket client) {
        FileInputStream fis = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try{
             fis = new FileInputStream(fileArrayList.get(0).getAbsolutePath());
             dis = new DataInputStream(fis);
             dos = new DataOutputStream(client.getOutputStream());
             System.out.println("::::::::::UPLOAD::::::::::");

            byte[] buffer = new byte[(int) fis.getChannel().size()+8192];
            int temp;
            while ((temp = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, temp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
