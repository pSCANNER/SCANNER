package edu.isi.misd.scanner.network.registry.web.errors;

public final class MethodNotAllowedException extends RuntimeException 
{

    public MethodNotAllowedException() {
    }

    public MethodNotAllowedException(String message) {
        super(message);
    }

    public MethodNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodNotAllowedException(Throwable cause) {
        super(cause);
    }
    
}
