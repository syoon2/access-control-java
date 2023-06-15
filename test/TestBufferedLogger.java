package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import ca.mta.iottestbed.logger.BufferedLogger;

/**
 * Unit tests for ca.mta.iottestbed.logger.BufferedLogger
 * 
 * @author Hayden Walker
 * @version 2023-06-15
 */
public class TestBufferedLogger {
    
    public TestBufferedLogger() {

    }

    /**
     * Assert that filling the logger beyond its capacity
     * doesn't cause an exception.
     */
    @Test
    public void testResize() {
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                BufferedLogger logger = new BufferedLogger(5);
                logger.log("The quick brown fox jumps over the lazy dog.");
            }
        });
    }
}
