package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class RefreshServer implements Serializable{
    private ServerSocket server;
    private Socket client;
    public static void main(String[] args){
        new RefreshServer();
    }
    public RefreshServer(){
        try{
            server = new ServerSocket(3124);
            System.out.println("DOWNLOAD SERVER STARTED");
            while(true) {
                client = server.accept();
                ThreadRefreshServer tds = new ThreadRefreshServer(client);
                tds.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

class ThreadRefreshServer extends Thread implements Serializable {
    private Socket client;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    ArrayList<String> tmp=null;
    int clientID = (-1);

    public ThreadRefreshServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {

        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());

            clientID = ois.readInt();
            while(true) {

                if ((new File("" + clientID).listFiles()) != null) {

                    tmp = new ArrayList<>();
                    File[] f = new File("" + clientID).listFiles();

                    for(File files:f)
                        tmp.add(files.getName());

                }

                if(tmp != null) {
                    oos.writeObject(tmp);
                    oos.flush();
                }
                Thread.sleep(5000);
            }
        } catch(SocketException s){
            System.err.println("CLIENT DISCONNECTED! DELETING FILES IN DIR : "+this.clientID);
            try {
                File[] clientFiles = new File("" + clientID).listFiles();
                for (File f : clientFiles) {
                    f.delete();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        } catch(IOException| InterruptedException e) {
            System.err.print("CLIENT => SERVER :: Irgend etwas ist schief gelaufen bei der Übertragung!:");
            e.printStackTrace();
        }
    }
}