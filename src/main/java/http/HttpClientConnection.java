package http;

import java.io.*;
import java.net.*;

//Thread for processing client request
public class HttpClientConnection extends Thread {

    private Socket socket;

    public HttpClientConnection(Socket socket){
        this.socket = socket;
    }


    @Override
    public void run(){

        InputStream inputStream = null;
        OutputStream outputStream = null;


        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            int _byte;

            // while ( (_byte = bufferedInputStream.read()) >=0 ){
            //     System.out.println((char)_byte);
            // }
    
            String html = "<html><title>http server</title><body><h1>this page was served by my server<h1></body></html>";
            final String CRLF = "\r\n";
            String response = 
                    "HTTP/1.1 200 OK" + CRLF + //http response code
                    "Content-Length: " +  html.getBytes().length + CRLF + //header
                    CRLF+
                    html+
                    CRLF+CRLF;

            outputStream.write(response.getBytes());

            System.out.println( "Processing of connection done");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream!= null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //TODO: handle exception
                }
            }
            if (outputStream!= null){
                try {
                    outputStream.close();
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }
            if (socket!= null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }
        }
    }
}
