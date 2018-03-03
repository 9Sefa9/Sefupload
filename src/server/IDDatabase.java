package server;

import java.io.File;
import java.sql.*;
import java.util.Random;
import java.util.UUID;

import static java.lang.Class.forName;

public class IDDatabase {

    private Connection con;
    private PreparedStatement pstmnt;
    private Statement stmnt;
    private ResultSet rs;
    private String clientInformation;
    private int newID = -1;
    public IDDatabase(String clientInformation) {
        this.clientInformation = clientInformation;
        loadDriver();
        createConnection();
    //    checkForExistingID();
    }
    public synchronized void loadDriver(){
        try {
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException  c){
            c.printStackTrace();
        }
    }
    public synchronized void createConnection(){
        try{
            con = DriverManager.getConnection("jdbc:sqlite:G:/Users/Progamer/Desktop/IDDatabase.db");
        }catch(Exception s){
            s.printStackTrace();
        }
    }

    public synchronized int createID(){
        try{
            stmnt = con.createStatement();
            rs = stmnt.executeQuery("SELECT ClientID, ClientInformation FROM IDTable;");
            //Falls ID im Datenbank vorhanden ist:
            while(rs.next()){
                if(rs.getString("ClientInformation").equals(this.clientInformation)){

                    pstmnt = con.prepareStatement("UPDATE IDTable SET ClientID = ? WHERE ClientInformation = ? AND ClientID = ?;");
                    int newerID = generateNewID();
                    pstmnt.setInt(1,newerID);
                    pstmnt.setString(2,this.clientInformation);
                    pstmnt.setInt(3,rs.getInt("ClientID"));

                    //lösche Ordner des Clienten am Server, da er jetzt ein neues ID hat:
                    File oldFolder= new File(""+rs.getInt("ClientID"));
                    if(oldFolder.exists()){
                        oldFolder.renameTo(new File(""+newerID));
                    }else if (!oldFolder.exists()){
                        new File(""+newerID).mkdir();
                    }


                    pstmnt.execute();

                    stmnt = con.createStatement();
                    rs = stmnt.executeQuery("SELECT ClientID, ClientInformation FROM IDTable;");
                    while(rs.next()){
                        if(rs.getString("ClientInformation").equals(this.clientInformation)){
                            this.newID = rs.getInt("ClientID");
                            break;
                        }
                    }
                    break;
                }
            }
            //falls es sich um ein neues User handelt:
            if(this.newID == -1) {
                pstmnt = con.prepareStatement("INSERT INTO IDTable (ClientID, ClientInformation) VALUES (?,?);");

                pstmnt.setInt(1, generateNewID());
                pstmnt.setString(2, this.clientInformation);

                pstmnt.execute();
                stmnt = con.createStatement();
                rs = stmnt.executeQuery("SELECT ClientID, ClientInformation FROM IDTable;");
                while(rs.next()){
                    if(rs.getString("ClientInformation").equals(this.clientInformation)){
                        this.newID = rs.getInt("ClientID");
                        break;
                    }
                }
            }

        }catch (SQLException s){
            s.printStackTrace();
        }finally{
            try{
                con.close();
                rs.close();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return this.newID;
    }
    public synchronized int generateNewID(){
            UUID id = UUID.randomUUID();
            char [] n = id.toString().substring(0,6).toCharArray();
            String newUID="";
            for(int i = 0; i<6; i++){
                if(Character.isDigit(n[i])){
                    newUID+=n[i];
                }
            }
            System.out.println(newUID);
        return Integer.parseInt(newUID);
    }
}
