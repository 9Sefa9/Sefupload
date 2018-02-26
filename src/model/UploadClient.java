package model;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

public class UploadClient extends Task<Void> {
    private Socket client;
    private Model model;
    private DataOutputStream dos = null;
    private FileInputStream fis = null;

    public UploadClient(Model model) {
        this.model = model;
        try {
            client = new Socket("localhost", 3122);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Void call() throws Exception {
        try {
            dos = new DataOutputStream(client.getOutputStream());
            //wie viele Elemente sollen verschickt werden ?
            dos.writeInt(model.getFileArrayList().size());
            dos.flush();

            for (File file : model.getFileArrayList()) {
                fis = new FileInputStream(file.getAbsolutePath());
                int size = (int) fis.getChannel().size();
                byte[] buffer = new byte[size + 2048];

                //wie groß ist ein File ?
                dos.writeInt(size);
                dos.flush();
                Long progressIndexL=null;
                int tmp;
                while ((tmp = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, tmp);
                    progressIndexL+=tmp;
                    updateProgress(progressIndexL,size);
                    //TODO vielleicht, hier einen dos.read setzen, wo der uploader den progress Index bestimmt ? raspb.
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //entferne File aus der Liste
                        model.getFileArrayList().remove(model.getFileArrayList().indexOf(file));
                    }
                });
                updateProgress(0,0);
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
