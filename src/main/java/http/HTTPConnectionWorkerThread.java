package http;

import java.io.*;
import java.net.*;

//Thread for processing client request
public class HTTPConnectionWorkerThread extends Thread {

    private Socket socket;

    public HTTPConnectionWorkerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){

        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
    
            String html = "<html><title>http server</title><body><h1>this page was served by my server<h1></body></html>";
            final String CRLF = "\r\n";
            String response = 
                    "HTTP/1.1 200 OK" + CRLF + //http response code
                    "Content-Length: " +  html.getBytes().length + CRLF + //header
                    CRLF+
                    html+
                    CRLF+CRLF;

            outputStream.write(response.getBytes());
            inputStream.close();
            outputStream.close();
            socket.close();

            try{
                sleep(5000);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            System.out.println( "Processing of connection done");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
