package http;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

//Thread for processing client request
public class HttpClientConnection extends Thread {

    private Socket socket;
    private String webroot;
    final String CRLF = "\r\n";
    
    public HttpClientConnection(Socket socket,String webroot){
        this.socket = socket;
        this.webroot = webroot;
    }

//open html file
    private String openHTML(String htmlDir){
        String line;
        String html="";
        List<String> listofString= new ArrayList<>();
        try (Reader reader = new FileReader(htmlDir)){
            BufferedReader br = new BufferedReader(reader);
            while (null != (line = br.readLine())){
                listofString.add(line);
            }
            for (String s : listofString){
                html += s.trim();
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
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

//concatenate 2 byte array
    private byte[] concat2ByteArray(byte[] header, byte[] content){
        byte[] c = new byte[header.length + content.length];
        System.arraycopy(header, 0, c, 0, header.length);
        System.arraycopy(content, 0, c, header.length, content.length);
        return c;
    }

//parse request
    private void parseRequest(String methodUsed, String queryUsed, OutputStream outputStream, Set<String> setofFile) throws Exception {
        HttpWriter httpWriter = new HttpWriter(outputStream);
        if (queryUsed.substring(1).isBlank()) queryUsed = "/index.html";

        if (!methodUsed.equals("GET")){ //check if get method is used
            httpWriter.writeString(res405(methodUsed));
        }
        else if (!setofFile.contains(queryUsed.substring(1).toLowerCase())) { //check if resource exists
            httpWriter.writeString(res404(queryUsed));
        }
        else{
            String resourceExt = queryUsed.substring(1).toLowerCase().split("\\.",2)[1];
            // System.out.println(resourceExt);
            byte[] header = res200(true).getBytes();
            if (resourceExt.equals("png")){
                BufferedImage imageRequested = ImageIO.read(new File(webroot+queryUsed));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(imageRequested, "png", bos );
                byte[] pngByte = bos.toByteArray();
                httpWriter.writeBytes(concat2ByteArray(header, pngByte));
                httpWriter.flush();
            }
            else if (resourceExt.equals("html") || resourceExt.equals("css") ){
                String html = openHTML(webroot+queryUsed);
                httpWriter.writeString(res200(false)+html);
                httpWriter.flush();
            }
        }
    }

    @Override
    public void run(){

        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        // BufferedWriter bufferedWriter = null;
        Set<String> setofFile = getFiles(this.webroot);

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream) );
            // bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            
            String header1stline = bufferedReader.readLine();
            StringTokenizer parseHeader1st = new StringTokenizer(header1stline);
            String requestMethod = parseHeader1st.nextToken().toUpperCase();
            String query = parseHeader1st.nextToken();

            parseRequest(requestMethod, query, outputStream,setofFile);
            System.out.println( "Processing of connection done");
        } 
        catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (bufferedReader!= null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream!= null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket!= null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
