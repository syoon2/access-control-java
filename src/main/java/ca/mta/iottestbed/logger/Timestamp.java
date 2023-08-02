package ca.mta.iottestbed.logger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * A timestamp.
 * 
 * @author Hayden Walker
 * @version 2023-06-15
 */
public class Timestamp {
    /**
     * Timestamp as String.
     */
    private String timestamp;

    /**
     * Create a new {@code Timestamp}.
     */
    public Timestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()); 
        timestamp = formatter.format(Instant.now());   
    }

    /**
     * Return the Timestamp as a String.
     * 
     * @return Timestamp as String.
     */
    @Override
    public String toString() {
        return timestamp;
    }   
}
