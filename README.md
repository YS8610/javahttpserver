# Java Http Server
A simple http server with thread written in java for serving html, css and png file only. Server only supports GET method.

## Operation
Default port for http server is 3000<br>
Default directory is in root ./static<br>

## Running Jar file
The Jar file can take in 2 optional command line arguments. If no command line arguments are given, then the default values will be used. Order does not matter.<br><br>
`--port <port number>` for specifying the listening port number for server with default of 3000<br>
`--docRoot <directory path>` for specifying the directory path folder where the html, css and png files can be found <br><br>

For example,
> This will run server with default listening port 3000 and default directory at root ./static
>
>>`java -jar javahttpserver.jar`  
<br>

>This will run server with listening port 5000 and default directory at root ./static
>
>>`java -jar javahttpserver.jar --port 5000`
<br>

>This will run server with default listening port 3000 and directory at root ./html
>
>>`java -jar javahttpserver.jar --docRoot html`
<br>

>This will run server with listening port 5000 and directory at root ./html
>
>>`java -jar javahttpserver.jar --docRoot html --port 5000`
