package ca.mta.iottestbed.logger;

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
     * Log a message.
     * 
     * @param message Message to log.
     */
    public void log(String message) {
        // increase size if necessary
        if(size + message.length() >= capacity - 3) {
            increaseLogSize();
        }

        // write message to buffer
        for(char character : message.toCharArray()) {
            buffer[size++] = character;
        }

        // add newline
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

    /**
     * Log multiple messages.
     * 
     * @param messages Messages to log.
     */
    @Override
    public void log(String... messages) {
        for(String message : messages) {
            log(message);
        }
    }
}
