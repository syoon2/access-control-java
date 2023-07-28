package ca.mta.iottestbed.meter;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.mta.iottestbed.logger.BufferedLogger;
import ca.mta.iottestbed.logger.Timestamp;
import ca.mta.iottestbed.logger.BufferedFileLogger;
import ca.mta.iottestbed.network.Connection;
import ca.mta.iottestbed.network.Listener;

/**
 * A smart meter that reads data from sensors over the network.
 * 
 * @author Hayden Walker
 * @version 2023-06-15
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
     * Logs for each sensor's data. Keys are sensor names,
     * and values are the logs.
     */
    private Map<Connection, BufferedFileLogger> messageLogs;

    /**
     * Meter's name.
     */
    private String name;

    /**
     * Log of network activity.
     */
    private BufferedLogger networkLog;

    /**
     * Create a new {@code Meter} object.
     * 
     * @param name Name of Meter.
     */
    public Meter(String name) {
        this.connections = Collections.synchronizedSet(new HashSet<Connection>());
        this.name = name;
        this.networkLog = new BufferedLogger();
        this.networkLog.timestampEnabled(true);
        this.messageLogs = Collections.synchronizedMap(new HashMap<Connection, BufferedFileLogger>());
    }
       
    /**
     * Establish a connection with a sensor at a certain IP address.
     * 
     * @param ip IP address.
     * @throws IOException if unable to connect to the specified address
     */
    private void addDevice(String ip) throws IOException {
        Connection connection = new Connection(ip, SENDING_PORT);
        connection.addLogger(networkLog);
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
        Listener listener = new Listener(LISTENING_PORT);
        listener.addLogger(networkLog);
        boolean active = true;

        while(active) {
            // read new socket
            Connection connection = listener.accept();
            connection.addLogger(networkLog);

            // get device id
            String id = connection.receive()[0];

            // add a message logger for this connection
            File csv = new File(id + ".csv");
            messageLogs.put(connection, new BufferedFileLogger(csv));

            // create new thread to listen to the socket
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        monitor(connection);
                    } catch (IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                }
            }).start();
        }

        listener.close();
    }

    /**
     * Listen to a connection.
     * 
     * Monitors a connection, and handles incoming messages. Stops listening
     * if a read fails.
     * 
     * @param socket Socket to listen to.
     * @throws IOException if an I/O error occurs
     */
    private void monitor(Connection connection) throws IOException {
        // listen while connection is active
        boolean active = true;
    
        while(active) {
            // read data from socket
            String[] data = connection.receive();        

            // check for failure
            if(data == null) {
                active = false;
            }

            // log readings
            else if(data[1].equals("report")) {
                // write to log
                messageLogs.get(connection).log(
                    new Timestamp() + "," +
                    data[2].substring(2, data[2].length()) + "," +
                    data[3].substring(2, data[3].length())
                );
            }

            // respond to ping
            else if(data[1].equals("ping")) {
                connection.send(name, "pong");
            }
        }

        // close and remove message logger
        messageLogs.get(connection).close();
        messageLogs.remove(connection);

        // close and remove connection
        connections.remove(connection);
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                // display readings periodically
                while(true) {
                    //System.out.println(name);
                    //displayReadings();
                    //networkLog.printFlush();
                    networkLog.printFlush();

                    // System.out.println("Active connections:");

                    // for(Connection connection : connections) {
                    //     System.out.println("\t" + connection.getIP());
                    // }

                    // flush all sensor logs
                    synchronized (messageLogs) {
                        for(BufferedFileLogger sensorLog : messageLogs.values()) {
                            sensorLog.write();
                        }
                    }

                    //TimeUnit.SECONDS.sleep(5);
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        
    }

    /**
     * Start the meter. Usage: java -jar Meter.jar [name] [sensor IP addresses ...]
     * 
     * @param args Usage: java -jar Meter.jar [name] [sensor IP addresses ...]
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // // attempt to start
        // try { 
        //     Meter meter1 = new Meter(args[0]);
        
        //     String[] ips = new String[args.length - 1];
            
        //     for(int i = 1; i < args.length; i++) {
        //         ips[i - 1] = args[i];
        //     }
            
        //     meter1.start(ips);
        // }

        // // display error message
        // catch(Exception e) {
        //     System.err.println("Usage: java -jar Meter.jar [name] [valid IPs ...]");
        // }

        Meter meter1 = new Meter("M1");
        meter1.start(new String[]{"127.0.0.1"});
    }
}
