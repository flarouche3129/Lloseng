// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

  ChatIF clientUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port)
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {//Modified for E7c) : Now receives the login id of the client and handles it.
    String message = msg.toString();
    if (message.startsWith("#login")) {
      System.out.println("Message received: " + msg + " from " + client);
      this.sendToAllClients(msg);
    }
    if  (message.startsWith("#")){
      String[] parameters = message.substring(1).split(" ");
      if (parameters[0].equalsIgnoreCase("login") && parameters.length > 1){
        if (client.getInfo("username") == null){
          client.setInfo("username", parameters[1]);
        }
        else{
          try{
            Object msgToSend = "Your username has already been set.";
            client.sendToClient(msgToSend);
          }catch (IOException e){
            System.out.println("Error while setting your username.");
          }
        }
      }
    }
    else{
      if (client.getInfo("username") == null){
        try{
          Object msgToSend = "You need a username before typing in the server.";
          client.sendToClient(msgToSend);
          client.close();
        }
        catch (IOException e){
          e.printStackTrace();
        }
      }
      else{
        System.out.println("Message received : " + msg + ", from " + client.getInfo("username"));
        Object msgToSend = client.getInfo("username") + ">" + message;
        this.sendToAllClients(msgToSend);
      }
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  protected void clientConnected(ConnectionToClient client){//method added for E5c) : Prints out a message when the client connects to the server
    System.out.println("Client has successfully connected to the server.");
  }

  synchronized protected void clientDisconnected(ConnectionToClient client){//method added for E5c) : Prints out a message when the client disconnects from the server.
    System.out.println(client.getInfo("username") + " has successfully disconnected from the server.");
    sendToAllClients(client.getInfo("username") + "has successfully disconnected from the server.");
  }

  synchronized protected void clientException(ConnectionToClient client, Throwable e){//method added for E5c) Prints out a message when an error happens with the client and disconnects the client from the server.
    try{
      client.close();
    }
    catch (IOException ex)
    {}
  }

  public void handleMessageFromServerConsole(String message) {//Created for E6c). The user can now type commands in the server.
    if (message.startsWith("#")) {
      String[] splittedMessage = message.split(" ");
      String command = splittedMessage[0];
      switch (command) {

        case ("#quit"):
          try {
            System.out.println("Quitting.");
            this.close();
          } catch (IOException e) {
            System.exit(1);
          }
          System.exit(0);
          break;

        case ("#stop"):
          this.stopListening();
          this.sendToAllClients("Server is now closed.");
          break;

        case ("#close"):
          try {
            System.out.println("Server disconnected from client");
            this.close();
          } catch (IOException e) {
            System.out.println("Couldn't disconnect from client.");
          }
          break;

        case ("#setport"):
          if (this.isListening() && this.getNumberOfClients() < 1) {
            super.setPort(Integer.parseInt(splittedMessage[1]));
            System.out.println("Port is now set to : " + Integer.parseInt(splittedMessage[1]));
          } else {
            System.out.println("The server is already running. You cannot set a port yet.");
          }
          break;

        case ("#start"):
          if (!this.isListening()) {
            try {
              this.listen();
            } catch (IOException e) {
              System.out.println("Error listening to client");
            }
          }
          break;

        case ("#getport"):
          System.out.println("The current port is : " + this.getPort());
          break;

        default:
          System.out.println("This command does not exist : '" + command + "'");
      }
    }
    else{
      this.sendToAllClients("SERVER MSG> " + message );
    }
  }
  //Class methods ***************************************************
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
