package ca.mta.iottestbed.sensor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ca.mta.iottestbed.tools.BufferedLogger;
import ca.mta.iottestbed.tools.NetworkUtils;

/**
 * A connected sensor.
 * 
 * @author Hayden Walker
 * @version 2023-06-13
 */
public class Sensor {
    
    /**
     * The sensor will listen for information from meters on this port.
     */
    private static final int LISTENING_PORT = 5005;

    /**
     * The sensor will send information to meters on this port.
     */
    private static final int SENDING_PORT = 5006;

    /**
     * Random number generator for generating sensor data.
     */
    private Random randomGenerator;

    /**
     * Maximum power consumption.
     */
    private int power;
    
    /**
     * Maximum water consumption.
     */
    private int water;

    /**
     * Name of sensor.
     */
    private String name;

    /**
     * Set of active connections.
     */
    private Set<Socket> connections;

    /**
     * Logger for sensor readings.
     */
    private BufferedLogger readings;
    
    /**
     * Logger for network messages.
     */
    private BufferedLogger networkLog;

    /**
     * Create a new Appliance object.
     * 
     * @param power Power consumption.
     * @param water Water consumption.
     */
    public Sensor(String name, int power, int water) {
        this.randomGenerator = new Random();
        this.name = name;
        this.power = power;
        this.water = water;
        this.connections = new HashSet<Socket>();
        this.readings = new BufferedLogger();
        this.networkLog = new BufferedLogger();
    }

    /**
     * Get the current power consumption.
     * 
     * @return The current water consumption.
     */
    private int getPower() {
        return randomGenerator.nextInt(power);
    }

    /**
     * Get the current water consumption.
     * 
     * @return The current water consumption.
     */
    private int getWater() {
        return randomGenerator.nextInt(water);
    }

    /**
     * Report sensor readings to connected meters.
     * 
     * Will call {@code getWater()} and {@code getPower()}, format the readings into
     * a {@link String}, and attempt to send that String to every {@link Socket} in {@code connections}.
     * 
     * If a send fails, will attempt to close the connection to the socket, and remove the socket
     * from {@code connections}.
     */
    private void reportReadings() {
        // get readings
        int water = getWater();
        int power = getPower();

        // log readings locally
        readings.log("Water: " + water + "; Power: " + power);

        // build message to send over network
        String message = NetworkUtils.buildMessage("give", "w:" + water, "e:" + power, name);
      
        // iterate over each connection
        Iterator<Socket> iterator = connections.iterator();
        while(iterator.hasNext()) {
            
            // get next connection
            Socket thisSocket = iterator.next();
            
            // attempt to send message
            if(NetworkUtils.writeSocket(thisSocket, message, networkLog)) {
                //networkLog.log("Sent " + message + " to " + thisSocket.getInetAddress() + ":" + thisSocket.getLocalPort());
            } 
            
            // close connection if failed
            else if(NetworkUtils.closeSocket(thisSocket)) {
                networkLog.log("Closed connection to meter " + thisSocket.getInetAddress() + ":" + thisSocket.getLocalPort());
                iterator.remove();
            } 
            
            // failed to close connection
            else {
                networkLog.log("Failed to close connection to meter " + thisSocket.getInetAddress() + ":" + thisSocket.getLocalPort());
            }
        }
    }

    /**
     * Listen for incoming connections.
     * 
     * @throws IOException
     */
    private void listen() throws IOException {
        
        ServerSocket listener = new ServerSocket(LISTENING_PORT);
        boolean active = true;

        while(active) {
            // accept an incoming connection
            Socket connectedSocket = listener.accept();
            networkLog.log("Received connection from " + connectedSocket.getInetAddress() + ":" + connectedSocket.getLocalPort());
            
            // read input
            String input = NetworkUtils.readSocket(connectedSocket, networkLog);
            String[] terms = input.split(NetworkUtils.separator);
            
            // if the connection wants to add a meter, add a meter
            if(terms[0].equals("addmeter")) {
                connections.add(new Socket(connectedSocket.getInetAddress(), SENDING_PORT));
                networkLog.log("Added meter " + connectedSocket.getInetAddress() + ":" + connectedSocket.getLocalPort());
            }
        }

        NetworkUtils.closeSocket(listener);
    }

    /**
     * Start the appliance.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void start() throws IOException, InterruptedException {
        // listen for connections in a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try { listen(); } catch(IOException e) {}
            }
        }).start();

        // report readings every 5 seconds
        while(true) {
            reportReadings();
            networkLog.printFlush();
            TimeUnit.SECONDS.sleep(5);
       }
    }

    /**
     * Start an Appliance.
     * 
     * @param args [power consumption] [water consumption]
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        //Appliance a1 = new Appliance(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        //Sensor a1 = new Sensor("A1", 10, 10);
        
        Sensor a1 = new Sensor(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        a1.start();
    }
}