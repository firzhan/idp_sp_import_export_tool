package org.wso2.sample.exception;

public class AdminServicesInvocationFailed extends RuntimeException {

    private static final long serialVersionUID = 234L;

    public AdminServicesInvocationFailed(String message) {
        super(message);
    }

    public AdminServicesInvocationFailed( String message, Exception ex) {
        super(ex);
    }
}
