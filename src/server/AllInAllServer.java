package server;

public class AllInAllServer {
    public static void main(String[]ar){
        new Thread(()->{
            new DownloadServer();
        }).start();
        new Thread(()->{
            new IDServer(3121);
        }).start();
        new Thread(()->{
            new UploadServer();
        }).start();

    }
}
