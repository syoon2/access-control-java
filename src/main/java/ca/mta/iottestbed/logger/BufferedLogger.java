package ca.mta.iottestbed.logger;

import java.util.Objects;

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

    /**
     * Store logged messages.
     */
    private char[] buffer;

    /**
     * Store number of characters stored in the log.
     */
    private int size;

    /**
     * Store the size of the buffer.
     */
    private int capacity;

    /**
     * Store whether or not to write timestamps.
     */
    private boolean timestampEnabled;

    /**
     * Create a new {@code BufferedLogger}.
     */
    public BufferedLogger() {
        this(DEFAULT_SIZE);
    }

    /**
     * Create a new {@code BufferedLogger} with the specified initial size.
     * 
     * @param size Initial size.
     * @throws IllegalArgumentException if {@code size <= 0}
     */
    public BufferedLogger(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Invalid initial buffer size: " + size);
        }
        this.size = 0;
        this.capacity = size;
        buffer = new char[this.capacity];
        timestampEnabled = false;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void log(String message) {
        // add a timestamp
        if(timestampEnabled) {
            logTimestamp();
        }

        // write message to buffer
        for(char character : Objects.toString(message).toCharArray()) {
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
    public synchronized String flush() {
        String output = new String(buffer, 0, size);
        size = 0;
        return output;
    }

    /**
     * Print and flush the buffer contents.
     */
    public synchronized void printFlush() {
        System.out.print(flush());
    }

    /**
     * Enable or disable timestamps.
     * 
     * @param status {@code true} to enable timestamps.
     */
    public synchronized void timestampEnabled(boolean status) {
        this.timestampEnabled = status;
    }

    /**
     * Check if the size needs to be increased, and call
     * {@link #increaseLogSize()} if it does.
     */
    private synchronized void checkSize() {
        if(size >= capacity - 1) {
            increaseLogSize();
        }
    }

    /**
     * Add a timestamp to the log.
     */
    private synchronized void logTimestamp() {
        char[] date = ("[" + new Timestamp().toString() + "] ").toCharArray();
        for(int i = 0; i < date.length; i++) {
            checkSize();
            buffer[size++] = date[i];
        }
    }

    /**
     * Increase the size of the buffer by a factor of 2.
     */
    private synchronized void increaseLogSize() {
        // calculate new size
        int newCapacity = 2 * capacity;

        // create a new buffer
        char[] newBuffer = new char[newCapacity];
        
        // copy buffer
        System.arraycopy(buffer, 0, newBuffer, 0, capacity);

        // replace buffer and capacity
        buffer = newBuffer;
        capacity = newCapacity;
    }
}
