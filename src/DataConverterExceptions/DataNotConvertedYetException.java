package DataConverterExceptions;

/**
 * Exception thrown when DataConverter is called to return converted Data before converting them
 *
 * @author Łukasz Mielczarek
 * @version 18.10.2016
 */
public class DataNotConvertedYetException extends Exception{
    /**
     * Constructor
     * @param s description of the exception
     */
    public DataNotConvertedYetException(String s){
        super(s);
    }
}
