package http;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class HttpMain 
{
// function for task 4
    static public boolean checkFolder(String pathDirectory){
        Path filePath = Paths.get(pathDirectory);
        if (Files.isDirectory(filePath) && Files.isReadable(filePath) && Files.exists(filePath) ){
            return true;
        }
        else{
            String isDir = Files.isDirectory(filePath)?"Path is a directory":"As Path is not a directory, System will exit";
            String isRead = Files.isReadable(filePath)?"Path is readable":"As Path is not readable, System will exit";
            String isExist = Files.exists(filePath)?"Path exists.":"As Path does not exist, System will exit";
            System.out.println(isDir+"\n"+isRead+"\n"+isExist);
            return false;
        }
    }
//command line arguments parser
    public static String[] cmdParser(String[] cmdarg){
        List<String> listofArg = new ArrayList<>();
        String port = "3000"; //default port
        String docRoot = "./static"; //default directory
        String[] portDir = {port,docRoot};
        if (cmdarg.length>1 && cmdarg!=null){
            listofArg = Arrays.asList(cmdarg);
            if (listofArg.size()==2){
                if (listofArg.contains("--port")) portDir[0] = listofArg.get(1);
                if (listofArg.contains("--docRoot")) portDir[1] = listofArg.get(1);
            }
            else if (listofArg.size()==4){
                int posofPort = listofArg.indexOf("--port");
                int posofWebRoot = listofArg.indexOf("--docRoot");
                portDir[0] = listofArg.get(posofPort+1);
                portDir[1] = listofArg.get(posofWebRoot+1);
            }
        }
        return portDir;
    }
//parse list of directory/directories
    static public List<String> parseDir(String paths){
        List<String> listofDir = new ArrayList<>();
        if (!paths.contains(":")){
            listofDir.add(paths);
        }
        else{
            Arrays.stream(paths.split(":")).forEach( (s) -> {if (!s.isBlank()) listofDir.add(s);} ); 
        }
        return listofDir;
    }
    
    public static void main( String[] args ){
        String[] commandArg = cmdParser(args);
        int port = Integer.parseInt(commandArg[0]); //no checking of integer. Pls note
        List<String> listofDir = parseDir(commandArg[1]);
        listofDir.forEach(s -> {
            if (!checkFolder(s)){
                System.exit(1);
            }
        });
        HttpServer server = new HttpServer(port,listofDir.get(0));
        server.startServer();
        System.out.println("Server starting at port no: "+port+" at directory: "+ listofDir.get(0));
    }
}
