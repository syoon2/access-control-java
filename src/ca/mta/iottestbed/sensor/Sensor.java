package ca.mta.iottestbed.sensor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ca.mta.iottestbed.logger.BufferedLogger;
import ca.mta.iottestbed.network.Connection;
import ca.mta.iottestbed.network.Listener;

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
    private HashSet<Connection> connections;
    
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
        this.connections = new HashSet<Connection>();
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
      
        // iterate over each connection
        Iterator<Connection> iterator = connections.iterator();

        while(iterator.hasNext()) {    
            // get next connection
            Connection thisSocket = iterator.next();
     
            // attempt to send message. if send fails, attempt to close.
            // if close is successful, remove connection.
            if(!thisSocket.send(name, "report", "w:" + water, "e:" + power) && thisSocket.close()) {
                iterator.remove();
            } 
        }
    }

    /**
     * Listen for incoming connections.
     * 
     * @throws IOException If attempt to open port fails.
     */
    private void listen() throws IOException {
        
        Listener listener = new Listener(LISTENING_PORT, networkLog);
        
        boolean active = true;

        while(active) {
            // accept an incoming connection
            Connection connection = listener.accept();
            
            // read input
            String[] terms = connection.receive();
            
            // if the connection wants to add a meter, add a meter
            if(terms[0].equals("addmeter")) {
                connections.add(new Connection(connection.getIP().toString(), SENDING_PORT, networkLog));
            }
        }

        listener.close();
    }

    /**
     * Start the appliance.
     * 
     * @throws IOException If unable to listen for connections.
     * @throws InterruptedException If listeneing thread is interrupted.
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