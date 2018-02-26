package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class IDServer {
    private ServerSocket server;
    private Socket client;

    public static void main(String[]args) {
         new IDServer(3121);
    }
    public IDServer(int port){
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
            //behandle einkommende requests
            String clientMessage0 = br.readLine();
            String clientMessage1 = br.readLine();
            System.out.println("SERVER => CLIENT :: "+ clientMessage0+" / "+clientMessage1+" entered Server Session!\n");
            switch(clientMessage0){
                case "requestID":{ IDDatabase idObj = new IDDatabase(clientMessage1);
                                   int newID = idObj.createID();
                                   bw.write(newID);
                                   bw.flush();
                                   System.err.println("SERVER NOTIFICATION :: New ID was sent to : "+clientMessage1);
                                   break;
                }

            }

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
