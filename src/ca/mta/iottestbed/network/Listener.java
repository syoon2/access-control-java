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
            log("Opened connection to " + incoming.getInetAddress() + ":" + incoming.getLocalPort());
            return new Connection(incoming);
        } 
        
        // log failure and return null
        catch(IOException e) {
            log("Failed to connect.");
            return null;
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
