package http;


public class HttpMain 
{
    public static void main( String[] args ){
        HttpServer server = new HttpServer(8080,"hello");
        server.startServer();
    }

}
