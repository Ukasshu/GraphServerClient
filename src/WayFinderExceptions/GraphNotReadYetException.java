package WayFinderExceptions;

/**
 * Exception thrown when GraphReader is called to return graph before reading it
 *
 * @author Łukasz Mielczarek
 * @version 03.11.2016
 */

public class GraphNotReadYetException extends Exception {
    public GraphNotReadYetException(String s){
        super(s);
    }
}
