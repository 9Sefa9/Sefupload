package server;

public class AllInAllServer {
    public static void main(String[]ar){
        new Thread(()->{
            new RefreshServer();
        }).start();
        new Thread(()->{
            new IDServer(3121);
        }).start();
        new Thread(()->{
            new UploadServer();
        }).start();
        new Thread(()->{
            new DownloadServer();
        });
    }
}
