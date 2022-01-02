package httpfileserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    int portno;
    List<String> webroot;

    public HttpServer(int portno, List<String> webroot){
        this.portno = portno;
        this.webroot = webroot;
    }

    public void startServer() throws IOException{
        ServerSocket serverSocket=null;
        Socket socket = null;
        serverSocket = new ServerSocket(this.portno);
        System.out.println("Server starting at port: "+ this.portno);

        try {
            while(!serverSocket.isClosed()){
                socket = serverSocket.accept();
                System.out.println("Client have connected");
                HttpClientConnection httpClientConnection =  new HttpClientConnection(socket,this.webroot);
                ExecutorService threadpool = Executors.newFixedThreadPool(3);
                threadpool.submit(httpClientConnection);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if(!serverSocket.isClosed()) {serverSocket.close();}
        }
    }
}
