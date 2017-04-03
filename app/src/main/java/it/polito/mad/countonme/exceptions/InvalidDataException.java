package it.polito.mad.countonme.exceptions;

/**
 * Exception raised when invalid data are provided
 * Created by francescobruno on 03/04/17.
 */

public class InvalidDataException extends Exception {

    public InvalidDataException() {
        super();
    }

    public InvalidDataException( String message ) {
        super( message );
    }
}
