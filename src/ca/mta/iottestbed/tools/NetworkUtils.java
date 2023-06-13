package ca.mta.iottestbed.tools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * An entity on a network.
 * 
 * @author Hayden Walker
 * @version 2023-06-13
 */
public abstract class NetworkUtils {
    // constants
    public static final String separator = "::_::";

    /**
     * Build a String to send over the network.
     * 
     * @param args Information to encode.
     * @return Message to send.
     */
    public static String buildMessage(String ... args){
        StringBuilder output = new StringBuilder();

        for(int i = 0; i < args.length; i++) {
            output.append(args[i]);

            if(i < args.length - 1) {
                output.append(separator);
            }
        }

        return output.toString();
    }


    /**
     * Close a socket.
     * 
     * @param socket Socket to close.
     * @return {@code true} if successful.
     */
    public static boolean closeSocket(Socket socket) {
        // attempt to close the socket
        try {
            socket.close();
            return true;
        } 
        
        // return false if failed
        catch(IOException e) {
            return false;
        }
    }

    /**
     * Close a server socket.
     * 
     * @param socket Socket to close.
     * @return {@code true} if successful.
     */
    public static boolean closeSocket(ServerSocket socket) {
        // attempt to close the socket
        try {
            socket.close();
            return true;
        } 
        
        // return false if failed
        catch(IOException e) {
            return false;
        }
    }

    /**
     * Read data from a socket.
     * 
     * @param socket Socket to read from.
     * @return Data read, or {@code null} if failed.
     */
    public static String readSocket(Socket socket) {
        // attempt to read
        try {
            String data = new DataInputStream(socket.getInputStream()).readUTF();
            System.out.println("Read " + data + " from " + socket.getInetAddress() + ":" + socket.getLocalPort());
            return data;
        }

        // catch failure to read
        catch(IOException e) {
            System.out.println("Failed to read from " + socket.getInetAddress() + ":" + socket.getLocalPort());
            return null;
        }
    }

    /**
     * Send a message over a socket.
     * 
     * @param socket Socket to send message over.
     * @param data Message to send.
     * @return {@code true} if successful.
     */
    public static boolean writeSocket(Socket socket, String data) {
        // attempt to write to the socket's output stream
        try {
            new DataOutputStream(socket.getOutputStream()).writeUTF(data);
            System.out.println("Sent " + data + " to " + socket.getInetAddress() + ":" + socket.getPort());
            return true;

        } 
        
        // return false if failed
        catch(IOException e) {
            System.out.println("Failed to send  " + data + " to " + socket.getInetAddress() + ":" + socket.getPort());
            return false;
        }
    }
}
