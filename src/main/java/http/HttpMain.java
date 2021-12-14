package http;

import java.io.*;


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
    static public String[] parseInput(String[] cmdlineArgs){
        String port = "8080"; //default port
        String docRoot = "./static"; //default directory
        String[] cmdArg = {port,docRoot};
        
        if (null==cmdlineArgs || cmdlineArgs.length == 1){
            return cmdArg;
        }
        else if (cmdlineArgs.length ==2|| cmdlineArgs.length ==3 && null!=cmdlineArgs){
            if (cmdlineArgs[0].equals("--port") )
            {
                cmdArg[0] = cmdlineArgs[1];
            }
            else if (cmdlineArgs[0].equals("--docRoot") ){
                cmdArg[1] = cmdlineArgs[1];
            }
        }
        else if (cmdlineArgs.length ==4 && null!=cmdlineArgs){
            if (cmdlineArgs[0].equals("--port")  && cmdlineArgs[2].equals("--docRoot") ){
                cmdArg[0] = cmdlineArgs[1];
                cmdArg[1] = cmdlineArgs[3];
            }
            else if (cmdlineArgs[2].equals("--docRoot") && cmdlineArgs[0].equals("--port")){
                cmdArg[0] = cmdlineArgs[3];
                cmdArg[1] = cmdlineArgs[1];
            }
        }
        System.out.println("Port: "+cmdArg[0] +" will be used at directory: "+ cmdArg[1]);
        return cmdArg;
    }


    public static void main( String[] args ){
        String[] commandArg = parseInput(args);

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
    }

}
