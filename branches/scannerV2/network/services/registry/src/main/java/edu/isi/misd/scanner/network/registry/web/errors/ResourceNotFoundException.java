package edu.isi.misd.scanner.network.registry.web.errors;

public final class ResourceNotFoundException extends RuntimeException 
{

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(Object ID) {
        super(
            "A resource could not be found matching the specified identifier: " + 
            ID.toString());
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }
    
}
