package edu.isi.misd.scanner.network.registry.web.errors;

public final class ConflictException extends RuntimeException 
{

    public ConflictException() {
    }

    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(Integer newId, Integer oldId) {
        super("The specified object ID (" + newId + 
              ") does not match referenced ID (" + oldId + ")");       
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictException(Throwable cause) {
        super(cause);
    }
    
}
