package ca.mta.iottestbed.logger;

/**
 * A logger, for writing messages to.
 * 
 * @author Hayden Walker
 * @version 2023-06-14
 */
public interface Logger {

    /**
     * Log a message.
     * 
     * @param messages Message to log.
     */
    public void log(String message);
}
