package server;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DownloadServer {
    private ServerSocket server;
    private Socket client;
    public static void main(String[] args){
        new DownloadServer();
    }
    public DownloadServer(){
        try{
            server = new ServerSocket(3123);
            client = server.accept();
            ThreadDownloadServer tds = new ThreadDownloadServer(client);
            tds.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

class ThreadDownloadServer extends Thread {
    private Socket client;

    public ThreadDownloadServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream())) {
            while (true) {
            //    ObservableList<File> tmp =
             //   oos.writeObject();
                //this.socket.setSoTimeout(5000);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}