package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class UploadServer {
    private ServerSocket server;
    private Socket client;
    public static void main(String[] args){
        new UploadServer();

    }
    private UploadServer(){
        try{
            server = new ServerSocket(3122);
            while(true) {
                System.out.println("UPLOAD SERVER STARTED");
                client = server.accept();
                Thread tus = new Thread(new ThreadUploadServer(client));
                tus.start();
                System.err.println("Client :: "+client.getInetAddress()+" entered upload session...");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}



class  ThreadUploadServer implements Runnable{
    private Socket client;
    private DataInputStream dis;
    private DataOutputStream dos;
    private FileOutputStream fos;
    public ThreadUploadServer(Socket client){
        this.client = client;
    }

    @Override
    public void run(){
            try{
                dis = new DataInputStream(client.getInputStream());
                int clientID = dis.readInt();
                int incomingListSize = dis.readInt();

               //@IDDatabase wird ein Ordner erstellt, wo die Dateien rein kommen.

                String clientDirectory = new File(""+clientID).getAbsolutePath();
                System.out.println(new File(clientDirectory).canRead());
                System.out.println(new File(clientDirectory).canWrite());
                System.out.println(new File(clientDirectory).canExecute());
                //fos wird in der While gesetzt.
               // fos = new FileOutputStream(clientDirectory);
               // dos = new DataOutputStream(fos);

                while(incomingListSize>0) {
                    String fileName = dis.readUTF();
                    //TODO irgendwas ist hier falsch!
                    fos = new FileOutputStream("G:\\Users\\Progamer\\Desktop"+"\\"+fileName);
                    dos = new DataOutputStream(fos);
                    int currentFileSize = dis.readInt();
                    int tmp;
                    byte[] buffer = new byte[currentFileSize];
                    while ((tmp = dis.read(buffer)) != -1) {
                        dos.write(buffer, 0, tmp);
                        dos.flush();
                    }
                    incomingListSize=-1;
                }
            }catch (IOException e){
                System.err.println("Something went wrong in UploadServer...");
                e.printStackTrace();
            }finally {
                try{
                    if(dis!=null)
                        dis.close();
                    if(dos!=null)
                        dos.close();
                    if(client!=null)
                        client.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
    }
}