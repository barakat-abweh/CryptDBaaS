/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 *
 * @author theblackdevil
 */
public class Server {
    private Database db;
    private final int numberOfThreads=200;
    private final int port=3000;
    private ArrayList<ServiceThread> pool;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private int index=0;
    private ServiceThread tempThread;
    private ExecutorService executor ;
    public Server() throws IOException
    {
        System.err.println("Thread\tQuery execution time");
        //this.initializeDB();
        this.initializePool();
        this.initializeServer();
        this.initializeExecuter();
    }
    
    private void initializePool() {
        this.pool=new ArrayList<>();
        int i=1;
        while(i<=this.numberOfThreads)
        {
            this.pool.add(new ServiceThread((i++)+""));
        }
    }
    private void initializeServer() throws IOException {
        this.serverSocket=new ServerSocket(port);
    }
    private void getConnections() throws IOException {
        this.clientSocket=this.serverSocket.accept();
        this.tempThread=this.pool.get(index);
        tempThread.setClient(clientSocket);
        this.executor.execute(tempThread);
        this.index=(++this.index)%this.pool.size();
    }
    private void initializeExecuter() {
        this.executor = Executors.newFixedThreadPool(this.numberOfThreads);
    }
    public static void main(String[] args) throws IOException {
        Server server= new Server();
        while(true){
            server.getConnections();
        }
    }
    private void initializeDB() {
        this.db=new Database();
    }
}
