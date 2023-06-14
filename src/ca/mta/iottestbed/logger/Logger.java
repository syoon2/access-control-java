package ca.mta.iottestbed.logger;

/**
 * A logger, for writing messages to.
 * 
 * @author Hayden Walker
 * @version 2023-06-14
 */
public interface Logger {

    /**
     * Log a series of messages.
     * 
     * @param messages Messages to log.
     */
    public void log(String ... messages);
}
