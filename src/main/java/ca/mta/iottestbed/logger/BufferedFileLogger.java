package ca.mta.iottestbed.logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BufferedFileLogger implements Closeable, Logger {
    /**
     * BufferedLogger to write logs to.
     */
    private BufferedLogger logger;

    /**
     * File to write to.
     */
    private FileWriter writer;

    /**
     * Create a new {@code BufferedFileLogger}.
     * 
     * @param file the destination file
     * @throws IOException if an I/O error occurs
     */
    public BufferedFileLogger(File file) throws IOException {
        logger = new BufferedLogger();
        // create a new FileWriter in 'append' mode
        writer = new FileWriter(file, true);
    }

    /** {@inheritDoc} */
    @Override
    public void log(String message) {
        logger.log(message);
    }

    /**
     * Empty the logger contents into file.
     * 
     * @return {@code true} if successful.
     */
    public boolean write() {
        // attempt to write to the file
        try {
            //writer.write(logger.flush());
            writer.write(logger.flush());
            writer.flush();
            return true;
        } 

        // return false if failed
        catch(IOException e) {
            return false;
        }
    }       
    
    /**
     * Close the BufferedFileLogger.
     * 
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        writer.close();
    }
}
