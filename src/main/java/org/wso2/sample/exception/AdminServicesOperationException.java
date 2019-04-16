package org.wso2.sample.exception;

public class AdminServicesOperationException extends RuntimeException {

    private static final long serialVersionUID = 234L;

    public AdminServicesOperationException(String message) {
        super(message);
    }

    public AdminServicesOperationException( String message, Exception ex) {
        super(ex);
    }
}
