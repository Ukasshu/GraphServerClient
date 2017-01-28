package MapReaderExceptions;

/**
 * Exception throw when MapReader is called to read Nodes again
 *
 * @author Łukasz Mielczarek
 * @version 16.10.2016
 */
public class NodesAlreadyReadException extends Exception{
    /**
     * Constructor
     * @param s description of the exception
     */
    public NodesAlreadyReadException(String s){
        super(s);
    }
}
