package ca.mta.iottestbed.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import ca.mta.iottestbed.logger.Logger;

/**
 * A facade for a Socket.
 * 
 * @author Hayden Walker
 * @version 2023-06-14
 */
public class Connection {
    
    /**
     * Delimits 'tokens', or Strings that make up a message.
     */
    private static final String separator = "::_::";

    /**
     * Socket to send/receive over.
     */
    private Socket socket;

    /**
     * Optional logger to write to.
     */
    private Logger logger;

    /**
     * Create a new Connection from a Socket.
     * 
     * @param socket Socket to wrap.
     */
    protected Connection(Socket socket) {
        this.socket = socket;
    }

    /**
     * Create a new, logged Connection from a Socket.
     * 
     * @param socket Socket to wrap.
     * @param logger Logger to write to.
     */
    protected Connection(Socket socket, Logger logger) {
        this(socket);
        this.logger = logger;
    }

    /**
     * Create a new Connection.
     * 
     * @param ip IP address.
     * @param port Network port.
     * @throws IOException If unable to connect.
     */
    public Connection(String ip, int port) throws IOException {
        this(new Socket(ip, port));
    }

    /**
     * Create a new, logged Connection.
     * 
     * @param ip IP address.
     * @param port Network port.
     * @param logger Logger to write to.
     * @throws IOException If unable to connect.
     */
    public Connection(String ip, int port, Logger logger) throws IOException {
        this(ip, port);
        this.logger = logger;
    }

    /**
     * Send a message over this connection.
     * 
     * @param tokens Tokens that make up the message.
     * @return {@code true} if successfully sent.
     */
    public boolean send(String ... tokens) {
        // build the message string
        String data = buildMessage(tokens);

        // attempt to write to the socket's output stream
        try {
            new DataOutputStream(socket.getOutputStream()).writeUTF(data);
            log("Sent " + data + " to " + getHost());
            return true;
        } 

        // return false if failed
        catch(IOException e) {
            log("Failed to send " + data + " to " + getHost());
            return false;
        }
    }

    /**
     * Receive a message over this connection.
     * 
     * @return Array of message tokens, or {@code null} if failed to read.
     */
    public String[] receive() {
        // attempt to read
        try {
            String data = new DataInputStream(socket.getInputStream()).readUTF();
            log("Received " + data + " from " + getLocalHost());
            return data.split(separator);
        }

        // catch failure to read
        catch(IOException e) {
            log("Failed to receive message from " + getLocalHost());
            return null;
        }
    }

    /**
     * Close the connection.
     * 
     * @return {@code true} if successful.
     */
    public boolean close() {
        // attempt to close
        try {
            socket.close();
            log("Closed connection to " + getLocalHost());
            return true;
        }
        
        catch(IOException e) {
            log("Failed to close connection to " + getLocalHost());
            return false;
        }
    }

    /**
     * Return the IP this Connection is connected to.
     * 
     * @return IP this Connection is connected to.
     */
    public String getIP() {
        return socket.getInetAddress().getHostAddress();
    }

    /**
     * Combine tokens into a String. Delimit tokens with the separator (constant).
     * 
     * @param tokens Tokens to build message from.
     * @return Message as String.
     */
    private String buildMessage(String ... tokens) {
        // use a StringBuilder
        StringBuilder out = new StringBuilder();

        // add each token
        for(int i = 0; i < tokens.length; i++) {
            out.append(tokens[i]);

            // separate using separator
            if(i < tokens.length - 1) {
                out.append(separator);
            }
        }

        // return string
        return out.toString();
    }

    /**
     * Write a message to the logger, if it exists.
     * 
     * @param message Message to log.
     */
    private void log(String message) {
        if(logger != null) {
            logger.log(message);
        }
    }

    /**
     * Return ip:localPort
     * 
     * @return ip:localPort
     */
    private String getLocalHost() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort();
    }

    /**
     * Return ip:port
     * 
     * @return ip:port
     */
    private String getHost() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }
}
