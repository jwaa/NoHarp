package leaptest.utils;

/**
 * Loggable Interface for logging states of classes
 * @author silvandeleemput
 */
public interface Loggable {
    
    /**
     * Gives the implementing class the Log object to write its state to
     * @param log Log object
     */
    public void log(Log log);
    
}
