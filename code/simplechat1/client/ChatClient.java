// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;
import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    if (message.startsWith("#")) {
      String[] splittedMessage = message.split(" ");
      String command = splittedMessage[0];
      switch (command) {
        case ("#quit"):
          quit();
          break;

        case ("#logoff"):
          try {
            System.out.println("The connection is now closed.");
            connectionClosed();
          } catch (Exception E) {//TODO very generic exception type, IOException doesn't work tho?
            System.out.println("Sorry, there was an error closing the connection.");
          }
          break;
        case ("#sethost"):
          if (this.isConnected()) {
            System.out.println("Already connected to a server! You can't change host right now.");
          } else {
            super.setHost(splittedMessage[1]);
          }
          break;
        case ("#setport"):
          if (this.isConnected()) {
            System.out.println("Already connected to a server! You can't change port right now.");
          } else {
            this.setPort(Integer.parseInt(splittedMessage[1]));
          }
          break;
        case ("#login"):
          if (this.isConnected()) {
            System.out.println("You are already logged in. Please log out before trying to log in again.");
          } else {
            try {
              this.openConnection();
            } catch (IOException e) {
              System.out.println("Could not establish the connection.");
            }
            break;
          }
        case ("#gethost"):
          System.out.println("The current host is : " + this.getHost());
          break;

        case ("#getport"):
          System.out.println("The current port is : " + this.getPort());
          break;

        default :
          System.out.println("This command does not exist : '" + command + "'" );
      }
    }
    else {
      try {
        sendToServer(message);
      } catch (IOException e) {
        clientUI.display
                ("Could not send message to server.  Terminating client.");
        quit();
      }
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  public void connectionClosed(){//Added for E5a) : will print out a message when the server closes, and will close the connection with the server.
    try{
      if(!isConnected()){
        closeConnection();
      }
    }catch(IOException e){
      connectionException(e);
    }
  }

  protected void connectionException (Exception ex){//added for E5a) : will respond to a shutdown by printing a message and closing.
    System.out.println("The server is now closed.");
    System.exit(0);
  }
}
//End of ChatClient class
