package MapReaderExceptions;

/**
 * Exception thrown when MapReader is called to return Ways before reading them
 *
 * @author Łukasz Mielczarek
 * @version 16.10.2016
 */
public class WaysNotReadYetException extends Exception {
    /**
     * Constructor
     * @param s description of the exception
     */
    public WaysNotReadYetException(String s){
        super(s);
    }
}
