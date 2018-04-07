package model;

import controller.Controller;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;

public class DownloadClient extends Task<Void> {
    private Controller controller;
    private String myObject;
    private Socket socket;
    private  int currentFileSize;
    private Long progressIndexL;
    public DownloadClient(Controller controller, String myObject){
        this.controller= controller;
        this.myObject = myObject;

        try{
            socket = new Socket("127.0.0.1",3125);
        }catch(IOException i){
            i.printStackTrace();
        }
    }
    @Override
    protected Void call() {
//        String fileName = controller.getDownloadList().getItems().get(controller.getDownloadList().getSelectionModel().getSelectedIndex());
        DataOutputStream dosLocal = null;
        FileOutputStream fosLocal = null;
        DataInputStream disServer = null;
        DataOutputStream dosServer = null;
        try{
            //Lokale Einstellungen
            String path = DownloadClient.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            fosLocal = new FileOutputStream(decodedPath + myObject);
            dosLocal = new DataOutputStream(fosLocal);

            //Serverübergreifende Einstellungen
            dosServer = new DataOutputStream(this.socket.getOutputStream());
            disServer = new DataInputStream(this.socket.getInputStream());

            dosServer.writeUTF(myObject);
            dosServer.flush();

            dosServer.writeInt(this.controller.getId());
            dosServer.flush();

            currentFileSize = disServer.readInt();

            int tmp;
            progressIndexL=0L;
            byte[] buffer = new byte[currentFileSize];
            while ((tmp = disServer.read(buffer)) != -1) {
                dosLocal.write(buffer, 0, tmp);
                dosLocal.flush();
                progressIndexL+=tmp;
                updateProgress(progressIndexL,currentFileSize);
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                //falls file vollständig runtergeladen
                if(progressIndexL == currentFileSize){
                    // lösche es aus der Liste, da beendet. Vielleicht eine bessere beschreibung ?
                    done();
                }else if (progressIndexL != currentFileSize){
                    cancel();
                }
                if(dosServer !=null)
                    dosServer.close();
                if(disServer != null)
                    disServer.close();
                if(dosLocal!=null)
                    dosLocal.close();
                if(fosLocal!=null)
                    fosLocal.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }


}
