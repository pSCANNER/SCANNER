package edu.isi.misd.scanner.network.registry.web.errors;

public final class ForbiddenException extends RuntimeException 
{

    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(Object userId, String reason) {         
        super(
            String.format(
                String.format(
                    "The user [%s] is not authorized to perform the operation." 
                    + ((reason != null) ? "  Reason: " + reason : ""),
                    userId.toString())));
    }
    
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }
    
}
