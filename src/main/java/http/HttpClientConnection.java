package http;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Thread for processing client request
public class HttpClientConnection extends Thread {

    private Socket socket;
    private String webroot;
    final String CRLF = "\r\n";
    
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

//return all files in the webroot folder
    private Set<String> getFiles(String dir){
        return Stream.of(new File(dir).listFiles())
        .filter(file -> !file.isDirectory())
        .map(File::getName)
        .collect(Collectors.toSet());
    }
//response405
    private String res405(String methodUsed){
        String response = "HTTP/1.1 405 Method is Not Allowed" + CRLF + CRLF + methodUsed +" not supported" +CRLF;
        return response;
    }
//response404
    private String res404(String resource){
        String response = "HTTP/1.1 404 Not Found" + CRLF + CRLF + resource +" not found" +CRLF;
        return response;
    }
//response200
    private String res200(Boolean picture){
        String response ="";
        if (picture){
            response = "HTTP/1.1 200 OK" + CRLF +"Content-Type: image/png" +CRLF+CRLF;
        }
        else {
            response = "HTTP/1.1 200 OK" + CRLF + CRLF;
        }
        return response;
    }


//parse request
    private void parseRequest(String methodUsed, String queryUsed, OutputStream outputStream, Set<String> setofFile) throws Exception {
        HttpWriter httpWriter = new HttpWriter(outputStream);
        if (queryUsed.substring(1).isBlank()) queryUsed = "/index.html";

        if (!methodUsed.equals("GET")){ //check if get method is used
            httpWriter.writeString(res405(methodUsed));
        }
        else if (!setofFile.contains(queryUsed.substring(1).toLowerCase())) { //check if resource exists
            System.out.println(queryUsed.substring(1).isBlank());
            httpWriter.writeString(res404(queryUsed));
        }
    }

    @Override
    public void run(){

        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        List<String> htmlList = readFileBuffer(this.webroot+"/index.html");
        String html1 = openHtmlFile(htmlList);
        Set<String> setofFile = getFiles(this.webroot);

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream) );
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            
            String header1stline = bufferedReader.readLine();
            StringTokenizer parseHeader1st = new StringTokenizer(header1stline);
            String requestMethod = parseHeader1st.nextToken().toUpperCase();
            String query = parseHeader1st.nextToken();
            
            setofFile.forEach(System.out::println);

             //get content of file in directory
            // setofFile.forEach(System.out::println);
            parseRequest(requestMethod, query, outputStream,setofFile);

            // check if get method is being used
            // if (!requestMethod.equals("GET")){
            //     String resp = "HTTP/1.1 405 Method is Not Allowed" + CRLF + CRLF + requestMethod +" not supported" +CRLF;
            //     outputStream.write(resp.getBytes());
            //     System.out.println("GET method not used. Method used is "+ requestMethod );
            // }
            // else{
                String html = "<html><link rel='stylesheet' href='style.css'><title>http server</title><body><h1>Hello World</h1><img src='./rainbow.png' width = 100vw><p><a href='./aboutme.html'>About me</a></p></body></html>";
                System.out.println(query.substring(1));
                String response = 
                        "HTTP/1.1 200 OK" + CRLF + //http response code
                        "Content-Length: " +  html.getBytes().length + CRLF + //header
                        CRLF+
                        html+
                        CRLF+CRLF;
                outputStream.write(response.getBytes());
            // }

            System.out.println( "Processing of connection done");
        } 
        catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if (bufferedReader!= null) {
                try {
                    bufferedReader.close();
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
