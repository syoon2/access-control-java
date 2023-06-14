package ca.mta.iottestbed.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ca.mta.iottestbed.tools.Logger;

/**
 * Facade for ServerSocket.
 * 
 * @author Hayden Walker
 * @version 2023-06-14
 */
public class Listener {
    
    /**
     * Socket to listen on.
     */
    private ServerSocket socket;

    /**
     * Port to listen on.
     */
    private int port;

    /**
     * Optional logger to write to.
     */
    private Logger logger;

    /**
     * Start listening on a port.
     * 
     * @param port Port to listen on.
     * @throws IOException If failed to open port.
     */
    public Listener(int port) throws IOException {
        socket = new ServerSocket(port);
        this.port = port;
    }

    public Listener(int port, Logger logger) throws IOException {
        this(port);
        this.logger = logger;        
    }

    /**
     * Accept an incoming connection.
     * 
     * @return New Connection, or {@code null} if failed.
     */
    public Connection accept() {
        // attempt to accept connection
        try {
            Socket incoming = socket.accept();
            log("Opened connection to " + incoming.getInetAddress().getHostAddress() + ":" + incoming.getLocalPort());
            
            if(logger != null) {
                return new Connection(incoming, logger);
            } else {
                return new Connection(incoming);
            }
        } 
        
        // log failure and return null
        catch(IOException e) {
            log("Failed to connect.");
            return null;
        }
    }  

    /**
     * Close this Listener.
     * 
     * @return {@code true} if successful.
     */
    public boolean close() {
        // log success
        try {
            socket.close();
            log("Closed listener on port " + port);
            return true;
        } 
        
        // log failure
        catch(IOException e) {
            log("Failed to close listener on port " + port);
            return false;
        }
    }

    /**
     * Write to the logger.
     * 
     * @param message Message to log.
     */
    private void log(String message) {
        if(logger != null) {
            logger.log(message);
        }
    }

}
