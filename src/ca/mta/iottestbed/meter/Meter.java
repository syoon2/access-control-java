package ca.mta.iottestbed.meter;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ca.mta.iottestbed.tools.BufferedLogger;
import ca.mta.iottestbed.tools.NetworkUtils;

/**
 * A smart meter that reads data from sensors over the network.
 * 
 * @author Hayden Walker
 * @version 2023-06-13
 */
public class Meter {

    /**
     * The port that the meter will listen on for information from sensors.
     */
    private static final int LISTENING_PORT = 5006;

    /**
     * The port that the meter will use to send information to sensors.
     */
    private static final int SENDING_PORT = 5005;

    /**
     * Set of active connections.
     */
    private Set<Socket> connections;

    /**
     * Meter's name.
     */
    private String name;

    private BufferedLogger readingLogger;

    /**
     * Create a new Meter object.
     * 
     * @param name Name of Meter.
     */
    public Meter(String name) {
        this.connections = new HashSet<Socket>();
        this.name = name;
        this.readingLogger = new BufferedLogger();
    }
       
    /**
     * Establish a connection with a sensor at a certain IP address.
     * 
     * @param ip IP address.
     * @throws IOException
     * @throws UnknownHostException If no such host exists.
     * @throws ConnectException If host refuses connection.
     */
    private void addDevice(String ip) throws ConnectException, IOException, UnknownHostException {
        Socket socket = new Socket(ip, SENDING_PORT);
        NetworkUtils.writeSocket(socket, "addmeter");
        connections.add(socket);
    }
    
    /**
     * Listen for new connections.
     * 
     * New connections will be listened to on a new thread.
     * 
     * @throws IOException If an IOException is encountered when opening or closing a socket.
     */
    private void listen() throws IOException {
        // ServerSocket to listen for incoming connections
        ServerSocket listener = new ServerSocket(LISTENING_PORT);
        boolean active = true;

        while(active) {
            // read new socket
            Socket socket = listener.accept();

            // create new thread to listen to the socket
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listenToSocket(socket);
                }
            }).start();
        }

        NetworkUtils.closeSocket(listener);
    }

    /**
     * Listen to a socket.
     * 
     * Monitors a socket, and handles incoming messages. Stops listening
     * if a read fails.
     * 
     * @param socket Socket to listen to.
     */
    private void listenToSocket(Socket socket) {
        // listen while connection is active
        boolean active = true;
    
        while(active) {
            // read data from socket
            String data = NetworkUtils.readSocket(socket, readingLogger);
            
            // stop when read fails
            if(data == null) {
                active = false;
            }
        }

        // close connection to socket
        NetworkUtils.closeSocket(socket);
    }

    public void start(String[] ips) throws UnknownHostException, IOException, InterruptedException {
        // add all ips
        // TODO: make this look for sensors
        for(String ip : ips) {
            addDevice(ip);
        }

        // listen for readings on a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try { listen(); } catch(IOException e) {}
            }
        }).start();

        System.out.println("Meter " + name + " started.");

        // display readings periodically
        while(true) {
            //System.out.println(name);
            //displayReadings();
            readingLogger.printFlush();
            TimeUnit.SECONDS.sleep(5);
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        Meter meter1 = new Meter(args[0]);
        
        String[] ips = new String[args.length - 1];
        
        for(int i = 1; i < args.length; i++) {
            ips[i - 1] = args[i];
        }

        meter1.start(ips);
    }
}
