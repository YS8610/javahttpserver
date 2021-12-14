package http;

import java.io.*;
import java.net.*;
import java.util.*;

//Thread for processing client request
public class HttpClientConnection extends Thread {

    private Socket socket;
    private String webroot;

    public HttpClientConnection(Socket socket,String webroot){
        this.socket = socket;
        this.webroot = webroot;
    }

// read index file and return as arraylist of string
    private List<String> readFileBuffer(String filename){
        String line;
        List<String> listofString= new ArrayList<>();
        try (Reader reader = new FileReader(filename)){
            BufferedReader br = new BufferedReader(reader);
            while (null != (line = br.readLine())){
                listofString.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listofString;
    }

//concatenate list of string together
    private String openHtmlFile( List<String> listofString){
        String html="";
        for (String s : listofString){
            html += s.trim();
        }
        return html;
    }
// check the request method
    private String getMethod(BufferedReader bufferedReader) throws IOException{
        String input = bufferedReader.readLine();
        StringTokenizer parse = new StringTokenizer(input);
        String method = parse.nextToken().toUpperCase();
        System.out.println(method);
        return method;
    }

    @Override
    public void run(){

        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        List<String> htmlList = readFileBuffer(this.webroot+"/index.html");
        String html1 = openHtmlFile(htmlList);
        final String CRLF = "\r\n";

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream) );

            // check if get method is being used
            if (getMethod(bufferedReader)!="GET"){
            //     String resp = "HTTP/1.1 405 Method is Not Allowed" + CRLF + CRLF + getMethod(bufferedReader) +" not supported" +CRLF;
            //     outputStream.write(resp.getBytes());
            // String a = getMethod(bufferedReader);
            System.out.println("GET method not used. Method used is "+ getMethod(bufferedReader) );
            }
            
            String html = "<html><link rel='stylesheet' href='style.css'><title>http server</title><body><h1>Hello World</h1><img src='./rainbow.png' width = 100vw><p><a href='./aboutme.html'>About me</a></p></body></html>";
            System.out.println(html1);
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
