package http;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Thread for listening for client connection done. 
public class ServerListenerThread extends Thread{

    private int port;
    private String webroot;
    private ServerSocket serverSocket;

    public ServerListenerThread (int port, String webroot) throws IOException {
        this.port = port;
        this.webroot = webroot;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run(){
        try {
            while(serverSocket.isBound() && !serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted at " + socket.getInetAddress());
                HttpClientConnection workerThread = new HttpClientConnection(socket,webroot); 
                ExecutorService threadPool = Executors.newFixedThreadPool(3);
                threadPool.submit(workerThread);
                // workerThread.start();
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if (serverSocket!=null){
                try {
                    serverSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
