import java.io.*;
import java.net.*;
import java.util.Properties;
import java.lang.ProcessBuilder;

public class Server {
  ServerSocket serverSocket;
  Socket connection = null;
  Properties prop;
  InputStream config = null;
  // Input streams
  InputStream istream;
  BufferedReader dstream;
  // Output streams
  OutputStream ostream;
  DataOutputStream dos;
  // Array list of programs
  String[] configProgArray;
  // The process builder for the program
  Process p;
  
  /**
   * This is an empty constructor of the Server.
   */
  Server() {
    // Do nothing
  }
  
  /**
   * This method is responsible with taking requests
   * from the client and processing them accordingly.
   * It begins by calling the parse method. To work
   * properly this assumes the command line argument
   * was of type properties. It then opens a ServerSocket
   * on port 4000 with a backlog and awaits a connection.
   * Upon connection it prints the host to the Server
   * output and opens streams. The input stream takes
   * a username, password, and program to execute from
   * the connected client. It then validates the 
   * username and password against the config.properties
   * file. If it is a valid user, it then checks whether
   * the user has permission to run the program. If so,
   * the server creates a ProcessBuilder for the program
   * and passes the output back to the client side.
   *
   * @param configFile The command line argument properties file.
   */
  void run(String configFile) {
    // Load the configurations for the server
    parse(configFile); 
    
    try {
      // Create the server socket on port 4000
      // Have a backlog of 10 cuz why not?
      serverSocket = new ServerSocket(4000, 10);
      System.out.println("Server is up.");
      
      // Wait for a server connection
      connection = serverSocket.accept();
      System.out.println("\nClient " + connection.getInetAddress().getHostName() + " is connected to server");
    
      // Set up the streams
      // Input
      istream = connection.getInputStream(); 
      dstream = new BufferedReader(new InputStreamReader(istream, "UTF-8"));
      // Output
      ostream = connection.getOutputStream();
      dos = new DataOutputStream(ostream);
      
      // Obtaining the username and password from client side
      String clientUser = dstream.readLine();
      String clientPass = dstream.readLine();
      String clientProg = dstream.readLine();
      
      // Validating client
      String password = prop.getProperty("user." + clientUser);
      if (password != null && password.equals(clientPass)) {
        // Correct validation of client
        System.out.println(clientUser + " is attempting to run " + clientProg + "\n");
        
        // See if program is valid
        // Search config.property file for the particular user's programs
        String configProgString = prop.getProperty("user." + clientUser + ".prog");
        if (configProgString != null) {
          // Parse the config.property string into programs array
          configProgArray = configProgString.split(",");
          
          // Search the array of allowed programs for the one attempted to access
          boolean allowed = false;
          for (int i = 0; i < configProgArray.length; i++) {
            if (clientProg.startsWith(configProgArray[i])) {
              allowed = true;
            }
          }
          
          // If the user is permitted, let them run the program
          if (allowed) {
            dos.writeBytes(clientUser + ", we are about to attempt running your program.\n");
            
            // Split the prog string into an array
            String[] clientProgArray = clientProg.split(" ");
            
            // Start the program in a process
            p = new ProcessBuilder(clientProgArray).start();
            
            // Make a stream to obtain the output of the process
            InputStream processStream = p.getInputStream();
            BufferedReader processBuffer = new BufferedReader( new InputStreamReader(processStream));
            
            // Send program output back to the client!
            String programOutput;
            dos.writeBytes("\nHere is the program output:\n");
            while ((programOutput = processBuffer.readLine()) != null) {
              dos.writeBytes(programOutput + "\n");
            }
          } else {
            // If the user doesn't have permission to run this program
            dos.writeBytes(clientUser + ", you do not have permission to run " + clientProg + "\n");
          }
        } else {
          // If the user doesn't have permission to run any programs
          dos.writeBytes(clientUser + ", you do not have access to any programs\n");
        }
      } else {
        // Incorrect validation of client
        dos.writeBytes("Incorrect credentials please try again\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        // Closing the streams
        dstream.close();
        istream.close();
        ostream.close();
        dos.close();
        
        // Closing the socket
        System.out.println("Closing server.\n");
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * This method loads a properties file as the Server's
   * configuration. 
   *
   * @param configFile The command arg config file.
   */
  void parse(String configFile) {
    prop = new Properties();
    try {
      config = new FileInputStream(configFile);
      
      // load the properties
      prop.load(config);
      
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (config != null) {
        try {
          config.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  /**
   * This method takes arguments from the command line.
   * The arguments from the command line should simply be
   * a configuration file of type properties. The main
   * method will check that there is exactly one argument,
   * and if so pass it to the run method.
   *
   * @param args The command line arguments.   
   */
  public static void main(String[] args) {
    Server server = new Server();
    
    if (args.length != 1) {
      System.out.println( "Run server by: ./server config-file");
    } else {
      while(true) {
        server.run( args[0] );
      }
    }
  }
}
