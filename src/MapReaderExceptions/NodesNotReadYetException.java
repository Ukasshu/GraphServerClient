package MapReaderExceptions;

/**
 * Exception thrown when MapReader is called to return read Nodes before reading them
 *
 * @author Łukasz Mielczarek
 * @version 16.10.2016
 */
public class NodesNotReadYetException extends Exception {
    /**
     * Constructor
     * @param s description of the exception
     */
    public NodesNotReadYetException(String s){
        super(s);
    }
}
