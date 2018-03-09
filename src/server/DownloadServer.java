package server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class DownloadServer implements Serializable{
    private ServerSocket server;
    private Socket client;
    public static void main(String[] args){
        new DownloadServer();
    }
    public DownloadServer(){
        try{
            server = new ServerSocket(3124);
            System.out.println("DOWNLOAD SERVER STARTED");
            while(true) {
                client = server.accept();
                ThreadDownloadServer tds = new ThreadDownloadServer(client);
                tds.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

class ThreadDownloadServer extends Thread implements Serializable {
    private Socket client;

    public ThreadDownloadServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        ArrayList<File> tmp=null;
        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());

            int clientID = ois.readInt();
            if((new File("" + clientID).listFiles())!=null ) {
                tmp= new ArrayList<>(Arrays.asList(new File("" + clientID).listFiles()));
            }

            oos.writeObject(tmp);
            oos.flush();

        } catch (IOException e) {
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