package MapReaderExceptions;

/**
 * Exception thrown when MapReader is called to read Ways after reading them
 *
 * @author Łukasz Mieczarek
 * @version 16.10.2016
 */
public class WaysAlreadyReadException extends Exception{
    /**
     * Constructor
     * @param s description of the exception
     */
    public WaysAlreadyReadException(String s){
        super(s);
    }
}
