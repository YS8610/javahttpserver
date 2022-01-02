package httpfileserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main 
{
    private static String[] cmdParser(String[] cmdargs){
        String port = "3000";
        String webroot = "./static";
        String[] parsedStrings = {port, webroot};

        if (cmdargs.length == 2){
            List<String> cmdlist = Arrays.asList(cmdargs);
            if (cmdlist.contains("--port")) {parsedStrings[0] = cmdlist.get(1); }
            if (cmdlist.contains("--docRoot")) {parsedStrings[1] = cmdlist.get(1); }
        }
        else if (cmdargs.length == 4){
            List<String> cmdlist = Arrays.asList(cmdargs);
            parsedStrings[0] = cmdlist.get(cmdlist.indexOf("--port")+1);
            parsedStrings[1] = cmdlist.get(cmdlist.indexOf("--docRoot")+1);
        }
        return parsedStrings;
    }

    private static List<String> webrootParser(String webroot){
        List<String> parsedWebroot = new ArrayList<>();
        if (webroot.contains(":")){
            parsedWebroot = Arrays.asList(webroot.split(":"));
        }
        else {
            parsedWebroot.add(webroot);
        }
        return parsedWebroot;
    }

    private static Boolean folderChecker(String dir){
        Path folderDir = Path.of(dir);
        Boolean pass = true;
        if (!Files.isDirectory(folderDir) && !Files.isReadable(folderDir) && !Files.exists(folderDir)){
            String isDir = Files.isDirectory(folderDir) ? dir + " is directory.": dir + " is not directory. System will exit."; 
            String isread = Files.isDirectory(folderDir) ? dir + " is readable.": dir + " is not readable. System will exit."; 
            String isExist = Files.isDirectory(folderDir) ? dir + " exists.": dir + " does not exist. System will exit.";
            System.out.println(isDir+"\n"+isread+"\n"+isExist); 
            pass = false;
            return pass;
        }
        else {
            return pass;
        }
    }

    public static void main( String[] args ) throws IOException {
        String[] parsedcmd = cmdParser(args);
        List<String> webRoot = webrootParser(parsedcmd[1]);
        
        for ( String s : webRoot){
            if(!folderChecker(s)){
                System.exit(1);
            }
        }
        int portno = Integer.parseInt(parsedcmd[0]);
        System.out.println("Port no used: " + portno);

        HttpServer httpServer = new HttpServer(portno, webRoot);
        httpServer.startServer();

    }
}
