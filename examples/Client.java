package examples;

 // import statements
 import java.net.*;
 import java.io.*;
 import java.util.Scanner;

 public class Client
 {
     // initializing socket and input output streams
     private DataOutputStream dataOut  = null;
     private Scanner sc                = null;
     private Socket skt                = null;
     // constructor to create a socket with given IP and port address
     public Client(String address, int port)
     {
         // Establishing connection with server
         try
         {
              // creating an object of socket
             skt = new Socket(address, port);
             System.out.println("Connection Established!! ");
             System.out.println("input \"Finish\" to terminate the connection. ");
             // taking input from user
             sc = new Scanner(System.in);
             // opening output stream on the socket
             dataOut = new DataOutputStream(skt.getOutputStream());
         }
         catch(UnknownHostException uh)
         {
             System.out.println(uh);
         }
         catch(IOException io)
         {
             System.out.println(io);
         }
         // to store the input messages given by the user
         String str = "";
         // The reading continues until "Finish" is input
         while (!str.equals("Finish"))
         {
             str = sc.nextLine(); // reading input
             try
             {
                 dataOut.writeUTF(str); // writing to the underlying output stream
             }
             // For handling errors while writing to output stream
             catch(IOException io)
             {
                 System.out.println(io);
             }
         }
         System.out.println(" Connection Terminated!! ");
         // for closing the connection
         try
         {
             dataOut.close();
             skt.close();
         }
         catch(IOException io)
         {
             System.out.println(io);
         }
     }
     public static void main(String argvs[])
     {
          // creating object of class Client
         new Client("localhost", 6666);
     }
 } 