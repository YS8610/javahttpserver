package http;

import java.io.*;
// import java.net.*;

// Server should be done.
public class HttpServer {

    int port;
    String webroot;

    public HttpServer (int port, String webroot){
        this.port = port;
        this.webroot = webroot;
    }

    public void startServer(){
        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(this.port, this.webroot);
            serverListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

