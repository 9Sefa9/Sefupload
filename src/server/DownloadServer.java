package server;

import java.io.*;
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
            server = new ServerSocket(3125);
            System.out.println("DOWNLOAD SERVER STARTED");
            while(true) {
                client = server.accept();
                Thread tus = new Thread(new ThreadDownloadServer(client));
                tus.start();
                System.err.println("Client :: "+client.getInetAddress()+" entered download session...");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
class ThreadDownloadServer extends Thread{
    private Socket client = null;
    public ThreadDownloadServer(Socket client){
        this.client = client;
    }
    @Override
    public void run(){
        DataInputStream dis = null;
        DataOutputStream dos = null;
        FileInputStream fis = null;
        try {
            dis = new DataInputStream(this.client.getInputStream());
            dos = new DataOutputStream(this.client.getOutputStream());
            String fileName = dis.readUTF();
            int clientID = dis.readInt();
            if(new File(""+clientID).listFiles()!=null){
                fis = new FileInputStream(""+clientID+"/"+fileName);
                System.out.println(""+clientID+"/"+fileName);
            }
            int size = (int) fis.getChannel().size();

            dos.writeInt(size);
            dos.flush();

            int tmp;
            byte[] buffer = new byte[size];
            while((tmp = fis.read(buffer))!= -1){
                dos.write(buffer,0,tmp);
                dos.flush();
            }

        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(dos!=null)
                    dos.close();
                if(dis != null)
                    dis.close();
                if(fis!=null)
                    fis.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
