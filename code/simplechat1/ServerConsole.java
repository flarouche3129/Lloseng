//This class was made for the E6b. It is very similar to the ClientConsole, with minor changes to adapt for the server,

import java.io.*;
import client.*;
import common.*;

import common.ChatIF;
import ocsf.server.*;


public class ServerConsole implements ChatIF{

    final public static int DEFAULT_PORT = 5555;

    EchoServer server;

    public ServerConsole(int port) {

        this.server = new EchoServer(port);

        try {
            this.server.listen();
            ;
        } catch (IOException e) {
            System.out.println("No client tried to connect!");
        }
    }

    public void accept() {
        try {
            BufferedReader fromConsole =
                    new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true) {

                message = fromConsole.readLine();
                server.handleMessageFromServerConsole(message);
//                if (!message.startsWith("#")){
//                    this.display(message);//maybe u don't need this
//                }
            }
        }
        catch (Exception ex){
            System.out.println("Unexpected error while reading the console!");
        }
    }

    @Override
    public void display(String message) {
        System.out.println("SERVER MSG> " + message);
    }

    public static void main(String[] args) {
        int port = 0;

        try{
            port = Integer.parseInt(args[0]);//Get port from command line
        }catch (Throwable t){
            port = DEFAULT_PORT;
        }
        ServerConsole server = new ServerConsole(port);
        server.accept();
    }
}
