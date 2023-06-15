package ca.mta.iottestbed.logger;

import java.util.Date;

/**
 * A buffered logger for storing diagnostic messages.
 * 
 * @author Hayden Walker
 * @version 2023-06-13
 */
public class BufferedLogger implements Logger {
    /**
     * Default buffer size.
     */
    public static final int DEFAULT_SIZE = 50;

    // store contents, size, and capacity
    private char[] buffer;
    private int size;
    private int capacity;

    /**
     * Create a new BufferedLogger.
     */
    public BufferedLogger() {
        size = 0;
        capacity = DEFAULT_SIZE;
        buffer = new char[DEFAULT_SIZE];
    }

    /**
     * Create a new BufferedLogger with a preset initial size.
     * 
     * @param size Initial size.
     */
    public BufferedLogger(int size) {
        this.size = 0;
        this.capacity = size;
        buffer = new char[this.capacity];
    }

    /**
     * Log a message.
     * 
     * @param message Message to log.
     */
    @Override
    public void log(String message) {
        // add a timestamp
        timeStamp();

        // write message to buffer
        for(char character : message.toCharArray()) {
            checkSize();
            buffer[size++] = character;
        }

        // add newline
        checkSize();
        buffer[size++] = '\n';
    }

    /**
     * Return the buffer contents and empty the buffer.
     * 
     * @return Buffer contents as a String.
     */
    public String flush() {
        String output = new String(buffer, 0, size);
        size = 0;
        return output;
    }

    /**
     * Print and flush the buffer contents.
     */
    public void printFlush() {
        System.out.print(flush());
    }

    /**
     * Check if the size needs to be increased, and call
     * increaseLogSize() if it does.
     */
    private void checkSize() {
        if(size >= capacity - 1) {
            increaseLogSize();
        }
    }

    /**
     * Add a timestamp to the log.
     */
    private void timeStamp() {
        char[] date = ("[" + new Date().toString() + "] ").toCharArray();
        for(int i = 0; i < date.length; i++) {
            checkSize();
            buffer[size++] = date[i];
        }
    }

    /**
     * Increase the size of the buffer by a factor of 2.
     */
    private void increaseLogSize() {
        // calculate new size
        int newCapacity = 2 * capacity;

        // create a new buffer
        char[] newBuffer = new char[newCapacity];
        
        // copy buffer
        for(int i = 0; i < capacity; i++) {
            newBuffer[i] = buffer[i];
        }

        // replace buffer and capacity
        buffer = newBuffer;
        capacity = newCapacity;
    }
}
