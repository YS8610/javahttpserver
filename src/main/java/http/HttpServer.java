package http;

import java.io.*;
import java.net.*;


public class HttpServer {


public static void main(String[] args) {
    int portno = 8080;

    try {
        ServerSocket serverSocket = new ServerSocket(portno);
        Socket socket = serverSocket.accept();

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
        serverSocket.close();
    } 
    catch (IOException e) {
        e.printStackTrace();
    }
}
  
}

