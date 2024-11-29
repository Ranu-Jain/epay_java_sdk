package com.sbi.epay.exceptions;

public class SBIEpayException extends Exception {

    public SBIEpayException(String message) {
        super(message);
    }

    public SBIEpayException(String message, Throwable cause) {
        super(message, cause);
    }

    public SBIEpayException(Throwable cause) {
        super(cause);
    }

    public SBIEpayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
