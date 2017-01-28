package MapReaderExceptions;

/**
 * Exception thrown when MapReader is called to read boundaries again
 *
 * @author Łukasz Mielczarek
 * @version 23.10.2016
 */
public class BoundsAlreadyReadException extends Exception {
    /**
     * Constructor
     * @param s description of the exception
     */
    public BoundsAlreadyReadException(String s){
        super(s);
    }
}
