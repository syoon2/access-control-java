package ca.mta.iottestbed.logger;

/**
 * An entity that can write to a Logger.
 * 
 * @author Hayden Walker
 * @version 2023-06-15
 */
public interface Loggable {
    /**
     * Add a Logger.
     * 
     * @param logger Logger to add.
     */
    public void addLogger(Logger logger);

    /**
     * Remove a Logger.
     * 
     * @param logger Logger to remove.
     */
    public void removeLogger(Logger logger);
}
