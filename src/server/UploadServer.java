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
            System.out.println("UPLOAD SERVER STARTED");
            while(true) {
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
    public void run() {
        try {
            dis = new DataInputStream(client.getInputStream());
            int clientID = dis.readInt();
            int targetClientID = dis.readInt();

            if (!new File("" + targetClientID).exists()) {
                //Ziel Ordner ist nicht erstellt worden, bzw der targetClient existiert nicht !
                return;
            } else {
                int incomingListSize = dis.readInt();

                //in @IDDatabase wird ein Ordner erstellt, wo die Dateien rein kommen.
                String targetClientDirectory = new File("" + targetClientID).getAbsolutePath();


                while (incomingListSize > 0) {
                    String fileName = dis.readUTF();

                    System.err.println("SERVER => CLIENT(TARGETID = " + targetClientID + ") FILENAME :: " + fileName);
                    fos = new FileOutputStream(targetClientDirectory + "\\" + fileName);
                    dos = new DataOutputStream(fos);
                    int currentFileSize = dis.readInt();
                    int tmp;
                    byte[] buffer = new byte[currentFileSize];
                    while ((tmp = dis.read(buffer)) != -1) {
                        dos.write(buffer, 0, tmp);
                        dos.flush();
                    }
                    incomingListSize = -1;
                }
            }
            }catch(IOException e){
                System.err.println("Something went wrong in UploadServer...");
                e.printStackTrace();
            }finally{
                try {
                    if (dis != null)
                        dis.close();
                    if (dos != null)
                        dos.close();
                    if (client != null)
                        client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    }
}