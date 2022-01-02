package httpfileserver;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

public class HttpClientConnection implements Runnable{

    Socket socket;
    List<String> webroot;
    private final String CLRF = "\r\n";

    public HttpClientConnection(Socket socket, List<String> webroot){
        this.socket = socket;
        this.webroot = webroot;
    }

    private String res405(String methodName){
        String res = "HTTP/1.1 405 Method Not Allowed" + CLRF + CLRF +
            methodName +" not supported" + CLRF;
        return res;
    }

    private String res404(String resourceName){
        String res = "HTTP/1.1 404 Not Found" + CLRF + CLRF +
            resourceName +" not found" + CLRF;
        return res;
    }

    private String htmlFile(String pathHTML){
        Path pathhtml = Path.of(pathHTML);
        String html="";
       try {
            html = Files.readAllLines(pathhtml).stream()
               .reduce("", (s0, s1) -> {return s0+s1;} );
            return html;
       } catch (IOException e) {
           System.out.println("cannot read file");
       }
        return html;
    }

    private String resExt(String resource){
        String ext="";
        String[] splitString = resource.split("\\.");
        if (splitString.length==2){
            ext = splitString[1];
            return ext;
        }
        else{
            return ext;
        }
    }

    private boolean checkResource(String resource){
        Path path = Path.of("./static"+resource);
        return (Files.exists(path) && Files.isRegularFile(path));
    }

    private byte[] pic(String dir) throws IOException {
        BufferedImage bImage = ImageIO.read(new File("./static"+ dir));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, resExt(dir) , bos);
        return bos.toByteArray(); 
    }

    public void run(){
        BufferedReader bufferedReader = null;
        HttpWriter httpWriter = null ;
        String header200 = "HTTP/1.1 200 OK" + CLRF + CLRF;

        try {
            bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
            httpWriter = new HttpWriter(socket.getOutputStream());
            
            String clientRequest = bufferedReader.readLine();
            StringTokenizer parsedRequest = new StringTokenizer(clientRequest);
            String methodRequest = parsedRequest.nextToken();
            String resourceRequest = parsedRequest.nextToken();

            if (!methodRequest.equals("GET")){
                httpWriter.writeString(res405(methodRequest));
                httpWriter.flush();
            }
            else if (resourceRequest.equals("/") || resourceRequest.equals("/index.html")){
                httpWriter.writeString( header200 + htmlFile("./static/index.html") );
                httpWriter.flush();
            }
            else if (!checkResource(resourceRequest)){
                httpWriter.writeString(res404(resourceRequest.substring(1)));
                httpWriter.flush();
            }
            else if (resExt(resourceRequest).equals("png")){
                byte[] picbyte = pic(resourceRequest);
                String headerPng = "HTTP/1.1 200 OK" + CLRF + "Content-Type: image/png" + CLRF + CLRF;
                byte[] headerPngByte = headerPng.getBytes();
                
                httpWriter.writeBytes(headerPngByte,0,headerPngByte.length);
                httpWriter.writeBytes(picbyte, 0, picbyte.length);
                httpWriter.flush();
            }
            else if (resExt(resourceRequest).equals("html")||resExt(resourceRequest).equals("css")){
                httpWriter.writeString( header200 + htmlFile("./static"+resourceRequest) );
                httpWriter.flush();
            }
            else if (resExt(resourceRequest).equals("pdf")){
                Path path = Path.of("./static"+resourceRequest);
                byte[] pathbyte = Files.readAllBytes(path);
                String headerPdf = "HTTP/1.1 200 OK" + CLRF 
                    +"Content-Length: "+ Integer.toString(pathbyte.length) + CLRF 
                    + "Content-Type: application/pdf" + CLRF + CLRF;
                httpWriter.writeBytes(headerPdf.getBytes(),0,headerPdf.getBytes().length);
                httpWriter.writeBytes(pathbyte,0,pathbyte.length);
                httpWriter.flush();
            }
            else if (resExt(resourceRequest).equals("mp4")){
                Path path = Path.of("./static"+resourceRequest);
                byte[] pathbyte = Files.readAllBytes(path);
                String headerMp4 = "HTTP/1.1 200 OK" + CLRF 
                    +"Content-Length: "+ Integer.toString(pathbyte.length) + CLRF 
                    + "Content-Type: application/mp4" + CLRF + CLRF;
                httpWriter.writeBytes(headerMp4.getBytes(),0,headerMp4.getBytes().length);
                httpWriter.writeBytes(pathbyte,0,pathbyte.length);
                httpWriter.flush();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try {
                if (bufferedReader!=null){ bufferedReader.close();}
                if (httpWriter!=null){ httpWriter.close();}
                if (!this.socket.isClosed()) { this.socket.close();}
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

// Content-Length: 744881
// application/pdf
// application/x-zip-compressed