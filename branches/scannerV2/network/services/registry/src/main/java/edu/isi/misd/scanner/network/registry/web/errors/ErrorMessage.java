package edu.isi.misd.scanner.network.registry.web.errors;

public class ErrorMessage
{
    private final int status;
    private final String message;
    private final String detail;

    public ErrorMessage(int status, String message) 
    {
        this(status, message, null);
    }
    
    public ErrorMessage(int status, String message, String detail) 
    {
        this.status = status;
        this.message = message;
        this.detail = detail;
    }

    public int getErrorStatusCode() {
        return status;
    }

    public String getErrorMessage() {
        return message;
    }
    
    public String getErrorDetail() {
        return detail;
    }    
}
