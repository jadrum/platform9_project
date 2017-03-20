import java.io.*;
import java.net.*;
import java.lang.Object;
import java.util.Arrays;

/**
 * This class is used to communicate with a server
 * which can run programs specified by the user of 
 * this program. The user must provide the IP address
 * of the server machine, a usernmae, and a password.
 * If successful, this class will output the output
 * of the program and the server is running on behalf
 * of the client.
 *
 * @author Justin Drum
 */
public class Client {
  // Socket for connection
  Socket clientSocket;
  // Output streams
  OutputStream ostream;
  DataOutputStream dos;
  // Input streams
  InputStream istream;
  BufferedReader dstream;
  
  /**
   * This is an empty constructor of the client.
   */
  Client(){
    // Do nothing
  }
  
  /**
   * This method is responsible for attempting to run
   * the program on the server. It takes four parameters.
   * First it attempts to make the socket via the IP address
   * parameter. This will fail given the wron IP or 
   * connection. Next it opens streams for input and output.
   * Then it passes the username, password, and program, all
   * on new lines to help with processing on server side.
   * If this is all successful, the client will continue 
   * reading lines from the server containing information
   * about the status of running the program with the given
   * credentials.
   *
   * @param ip The IP address of the server machine.
   * @param user The user of the client program.
   * @param pass The user's password.
   * @param prog The string containing the program to be run.
   */
  void run(String ip, String user, String pass, String prog) {
    try {
      // SET UP THE CONNECTION
      // Uses ip specified, this will fail given the incorrect IP address on the network
      clientSocket = new Socket(ip, 4000);
      
      // Make the communication streams
      // Output
      ostream = clientSocket.getOutputStream();
      dos = new DataOutputStream(ostream);
      // Input
      istream = clientSocket.getInputStream(); 
      dstream = new BufferedReader(new InputStreamReader(istream, "UTF-8"));
            
      // sending the user name and password
      dos.writeBytes(user + "\n");
      dos.writeBytes(pass + "\n");
      dos.writeBytes(prog + "\n");
      
      // Notify client that they are beginning to run program
      System.out.println(dstream.readLine());
      
      // Programs output
      String line;
      while ((line = dstream.readLine()) != null) {
        System.out.println(line);
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        // Close streams 
        dos.close();
        ostream.close();
        dstream.close();
        istream.close();
        
        // Close the client socket
        System.out.println( "\nClosing the client side." );
        clientSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }  
  
  /**
   * This method takes arguments from the command line.
   * The arguments should be: the ip address of the server,
   * the client's username and password specified in the 
   * properties file, as well as the program and arguments
   * to be executed.
   * The program and args part should be written just as 
   * you would if you were attempting to run the program
   * from command line on the client side.
   * So java programs should specify "java", C programs
   * should begin with "./" as needed, and so on.
   * The main method appends the program and arguments into
   * one string to easily pass it to the run method.
   *
   * @param args The command line arguments mentioned above.
   */
  public static void main(String args[]) {
    // Check if there are atleast 4 command line args
    if (args.length < 4) {
      System.out.println( "Run client by: ./client <server ip> <username> <password> <program> <args>");
    }
    // Create a new instance of the client...
    Client client = new Client();
    
    // Create a string builder containing all the args after the password
    StringBuilder sb = new StringBuilder();
    sb.append(args[3]);
    for (int i = 4; i < args.length; i++) {
      // Separate the args by spaces
      sb.append(" " + args[i]);
    }
    
    // Run the client 
    client.run(args[0], args[1], args[2], sb.toString());
  }
}