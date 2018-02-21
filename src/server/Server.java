package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket server;
    private Socket client;

    public static void main(String[]args) {
       Server server = new Server(3121);
    }
    public Server(int port){
        try {
            server = new ServerSocket(port);
            System.out.println("SERVER STARTED");
            while(true) {
                client = server.accept();
                ThreadClass t = new ThreadClass(client);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class ThreadClass extends Thread{
    private Socket client;
    private BufferedReader br;
    private BufferedWriter bw;
    public ThreadClass(Socket client){
        this.client = client;
    }
    @Override
    public void run(){
        try{
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            bw.write(31213);
            bw.flush();

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(br != null)
                br.close();
                if(bw!=null)
                bw.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
