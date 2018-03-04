package model;

import controller.Controller;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

public class UploadClient extends Task<Void> {
    private Socket client;
    private Model model;
    private Controller controller;
    private DataOutputStream dos = null;
    private FileInputStream fis = null;

    public UploadClient(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
        try {
            client = new Socket("localhost", 3122);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Void call(){
        try {
            dos = new DataOutputStream(client.getOutputStream());
            //ID des Clienten an Server(SQL)?
            dos.writeInt(controller.getId());
            dos.flush();

            //ID des anderen Clienten (Ziel Client)
            dos.writeInt(Integer.parseInt(controller.getTextFieldID().getText()));
            dos.flush();

            //wie viele Elemente sollen verschickt werden ?
            dos.writeInt(model.getFileArrayList().size());
            dos.flush();

            for (File file : model.getFileArrayList()) {
                dos.writeUTF(file.getName());
                dos.flush();
                fis = new FileInputStream(file.getAbsolutePath());
                int size = (int) fis.getChannel().size();
                byte[] buffer = new byte[size + 2048];

                //wie groß ist ein File ?
                dos.writeInt(size);
                dos.flush();
                Long progressIndexL=0L;
                int tmp;
                while ((tmp = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, tmp);
                    progressIndexL+=tmp;
                    updateProgress(progressIndexL,size);
                }
                updateProgress(0,0);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized(model.getFileArrayList()){
                            while (model.getFileArrayList().indexOf(file)!= -1)
                            model.getFileArrayList().remove(model.getFileArrayList().indexOf(file));
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dos != null)
                    dos.close();
                if (fis != null)
                    fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
