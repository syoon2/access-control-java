package ca.mta.iottestbed.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import ca.mta.iottestbed.logger.Loggable;
import ca.mta.iottestbed.logger.Logger;

/**
 * Facade for ServerSocket.
 * 
 * @author Hayden Walker
 * @version 2023-06-14
 */
public class Listener implements Closeable, Loggable {
    
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
    private HashSet<Logger> loggers;

    /**
     * Start listening on a port.
     * 
     * @param port Port to listen on.
     * @throws IOException If failed to open port.
     */
    public Listener(int port) throws IOException {
        this.socket = new ServerSocket(port);
        this.port = port;
        this.loggers = new HashSet<Logger>();
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
            
            // create a new Connection
            return new Connection(incoming);
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
     * @throws IOException  if an I/O error occurs while closing the underlying
     *                      socket
     */
    @Override
    public void close() throws IOException {
        // log success
        try {
            socket.close();
            log("Closed listener on port " + port);
        } 
        // log failure
        catch(IOException e) {
            log("Failed to close listener on port " + port);
            throw new IOException("Failed to close listener on port " + port, e);
        }
    }

    /**
     * Write to the logger.
     * 
     * @param message Message to log.
     */
    private void log(String message) {
        for(Logger logger : loggers) {
            logger.log(message);
        }
    }
    
    /**
     * Add a Logger to this Listener object. The Listener
     * will write logs to this Logger.
     * 
     * @param logger Logger to add.
     */
    @Override
    public void addLogger(Logger logger) {
        loggers.add(logger);
    }

    /**
     * Remove a Logger from this Listener object. The
     * Listener will no longer write to a Logger after
     * it is removed.
     * 
     * @param logger Logger to remove.
     */
    @Override
    public void removeLogger(Logger logger) {
        loggers.add(logger);
    }

}
