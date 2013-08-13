package edu.isi.misd.scanner.network.registry.web.errors;

public final class ResourceNotFoundException extends RuntimeException 
{

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }
    
}
