package MapReaderExceptions;

/**
 * Exception thrown when MapReader is called to return boundaries before reading them
 *
 * @author Łukasz Mielczarek
 * @version 23.10.2016
 */
public class BoundsNotReadYetException extends Exception {
    /**
     * Constructor
     * @param s description of the exception
     */
    public BoundsNotReadYetException(String s){
        super(s);
    }
}
