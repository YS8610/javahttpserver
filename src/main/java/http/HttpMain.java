package http;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class HttpMain 
{
    // function for task 4
    static public boolean checkFolder(String pathDirectory){
        File file = new File(pathDirectory);
        if (file.isDirectory()){
            return true;
        }
        else{
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

    public static void main( String[] args ){
        // String[] commandArg = parseInput(args);
        String[] commandArg = cmdParser(args);
        int port = Integer.parseInt(commandArg[0]); //no checking of integer. Pls note
        String dir = "./static";
        if (checkFolder(dir)){
            dir = commandArg[1];
        }
        else {
            System.exit(1);
        }

        HttpServer server = new HttpServer(port,dir);
        server.startServer();
        System.out.println("Server starting at port no: "+port+" at directory: "+ dir);
    }
}
