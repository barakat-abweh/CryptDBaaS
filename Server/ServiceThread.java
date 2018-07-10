/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import Encryption.RSA_Enc_Dec;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author theblackdevil
 */
class ServiceThread extends Thread{
    private DataOutputStream send;
    private DataInputStream receive;
    private boolean busy=false;
    private String cipherText;
    private String query="hello";
    private Database db;
    private char queryType;
    private ResultSet result;
    private long startTime;
    private long endtTime;
    private long sendtTime;
    private long startTime1;
    private long endTime1;
    public ServiceThread(String name)
    {
        super("Thread #"+name);
    }
    @Override
    public void run() {
        try {
            this.db=new Database();
            this.db.connect();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        long startTime=System.nanoTime();
        long endtTime=System.nanoTime();
        try {
            this.receiveCipher();
            this.startTime=System.nanoTime();
            this.decryptCipher();
            this.recognizeQuery();
        } catch (IOException ex) {
            Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.db.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(this.getName()+" :\t"+((double)(this.endtTime-this.startTime+endTime1-startTime1)/1000000000.0));
    }
    boolean getStatus()
    {
        return this.busy;
    }
    void setClient(Socket clientSocket) throws IOException {
        this.send=new DataOutputStream(clientSocket.getOutputStream());
        this.receive=new DataInputStream(clientSocket.getInputStream());
    }
    @Override
    public String toString() {
        return super.getName();
    }
    
    private void receiveCipher() throws IOException {
        this.cipherText=new String(this.receive.readUTF());
    }
    private void decryptCipher() {
        RSA_Enc_Dec rsa=new RSA_Enc_Dec();
        rsa.setCipherText(this.cipherText);
        rsa.decryptRSA();
        this.query=rsa.getPlainText();
    }
    private void recognizeQuery() {
        try {
            this.queryType=this.query.toLowerCase().toCharArray()[0];
            switch(this.queryType){
                case 's':this.endtTime=System.nanoTime();
                this.result=db.select(this.query);
                this.sendResult();
                break;
                case 'i':db.insert(this.query);
                this.endtTime=System.nanoTime();
                break;
                case 'u':db.update(this.query);
                this.endtTime=System.nanoTime();
                break;
                case 'd':db.delete(this.query);
                this.endtTime=System.nanoTime();
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendResult() {
        int columnCount=db.getColumnCount(this.result);
        StringBuilder res=new StringBuilder();
        try {
            while(this.result.next()){
                for(int i=1;i<columnCount;i++){
                    try {
                        res.append(this.result.getString(i)+",,,");
                    } catch (SQLException ex) {
                        Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                res.append(this.result.getString(columnCount)+"\n");
            }
            this.result.close();
            int mtu=1500;
            int index=0;
            startTime1=System.nanoTime();
            while(true){
                if(index+mtu>=res.length()){
                    if(index<res.length()){
                        try {
                            this.send.writeUTF(res.substring(index, res.length()));
                            this.send.writeUTF("end");
                        } catch (IOException ex) {
                            Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    }else{
                        try {
                            this.send.writeUTF("end");
                        } catch (IOException ex) {
                            Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    }
                }
                try {
                    this.send.writeUTF(res.substring(index,index+mtu));
                } catch (IOException ex) {
                    Logger.getLogger(ServiceThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                index+=mtu;
            }
            endTime1=System.nanoTime();
        } catch (SQLException ex) {
            this.recognizeQuery();
        }
    }
}
