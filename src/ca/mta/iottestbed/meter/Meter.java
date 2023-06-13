package ca.mta.iottestbed.meter;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ca.mta.iottestbed.tools.NetworkUtils;

/**
 * A smart meter that reads data from sensors.
 * 
 * @author Hayden Walker
 * @version 2023-06-13
 */
public class Meter {

    // define ports
    private static final int LISTENING_PORT = 5006;
    private static final int SENDING_PORT = 5005;

    // appliances to listen to
    private Set<Socket> appliances;
    private String name;

    /**
     * Create a new Meter object.
     * 
     * @param name Name of Meter.
     */
    public Meter(String name) {
        this.appliances = new HashSet<Socket>();
        this.name = name;
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
        appliances.add(socket);
    }
    
    /**
     * Listen for new connections.
     * 
     * New connections will be listened to on a new thread.
     * 
     * @throws IOException If an IOException is encountered when opening or closing a socket.
     */
    private void listen() throws IOException {
        ServerSocket listener = new ServerSocket(LISTENING_PORT);
        boolean active = true;

        while(active) {
            // read new socket
            Socket socket = listener.accept();

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
            String data = NetworkUtils.readSocket(socket);
            
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

        // display readings periodically
        while(true) {
            System.out.println(name);
            //displayReadings();
            TimeUnit.SECONDS.sleep(5);
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        Meter meter1 = new Meter("M1");
        meter1.start(new String[]{"127.0.0.1"});
    }
}
