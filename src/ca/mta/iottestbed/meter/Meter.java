package ca.mta.iottestbed.meter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ca.mta.iottestbed.logger.BufferedLogger;
import ca.mta.iottestbed.network.Connection;
import ca.mta.iottestbed.network.Listener;

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
    private Set<Connection> connections;

    /**
     * Meter's name.
     */
    private String name;

    private BufferedLogger networkLog;

    /**
     * Create a new Meter object.
     * 
     * @param name Name of Meter.
     */
    public Meter(String name) {
        this.connections = new HashSet<Connection>();
        this.name = name;
        this.networkLog = new BufferedLogger();
    }
       
    /**
     * Establish a connection with a sensor at a certain IP address.
     * 
     * @param ip IP address.
     */
    private void addDevice(String ip) throws IOException {
        Connection connection = new Connection(ip, SENDING_PORT, networkLog);
        connection.send("addmeter");
        connections.add(connection);
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
        Listener listener = new Listener(LISTENING_PORT, networkLog);
        boolean active = true;

        while(active) {
            // read new socket
            Connection connection = listener.accept();

            // create new thread to listen to the socket
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listenToConnection(connection);
                }
            }).start();
        }

        listener.close();
    }

    /**
     * Listen to a socket.
     * 
     * Monitors a socket, and handles incoming messages. Stops listening
     * if a read fails.
     * 
     * @param socket Socket to listen to.
     */
    private void listenToConnection(Connection connection) {
        // listen while connection is active
        boolean active = true;
    
        while(active) {
            // read data from socket
            String[] data = connection.receive();
            
            // stop when read fails
            if(data == null) {
                active = false;
            }
        }

        // close connection to socket
        connection.close();
    }

    public void start(String[] ips) throws IOException, InterruptedException {
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
            networkLog.printFlush();
            TimeUnit.SECONDS.sleep(5);
        }
    }

    /**
     * Start the meter. Usage: java -jar Meter.jar [name] [sensor IP addresses ...]
     * 
     * @param args Usage: java -jar Meter.jar [name] [sensor IP addresses ...]
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Meter meter1 = new Meter(args[0]);
        
        String[] ips = new String[args.length - 1];
        
        for(int i = 1; i < args.length; i++) {
            ips[i - 1] = args[i];
        }
        
        meter1.start(ips);

        //Meter meter1 = new Meter("M1");
        //meter1.start(new String[]{"127.0.0.1"});

    }
}
