package model;

import java.io.*;
import java.net.Socket;

public class Uploader extends Thread{
    private Socket client;
    public Uploader(){
        try {
            client = new Socket("localhost",3122);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        DataOutputStream dos;
        DataInputStream dis;
        FileOutputStream fos;
        FileInputStream fis;


            try{
                fis = new FileInputStream(");
            }catch (IOException e){
                e.printStackTrace();
            }
    }
}
