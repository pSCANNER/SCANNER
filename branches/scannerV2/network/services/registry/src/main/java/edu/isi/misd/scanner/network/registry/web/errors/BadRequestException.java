package edu.isi.misd.scanner.network.registry.web.errors;

import java.util.Collection;

public final class BadRequestException extends RuntimeException 
{

    public BadRequestException() {
    }
    
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }
    
    public BadRequestException(Collection badParams) {
        super("Invalid parameter(s) specified: " + badParams);
    }    
}
