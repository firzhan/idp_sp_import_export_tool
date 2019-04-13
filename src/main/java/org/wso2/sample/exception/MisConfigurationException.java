package org.wso2.sample.exception;

public class MisConfigurationException extends Exception  {

    private static final long serialVersionUID = 234L;

    public MisConfigurationException(String message) {
        super(message);
    }

    public MisConfigurationException( Exception ex) {
        super(ex);
    }
}
