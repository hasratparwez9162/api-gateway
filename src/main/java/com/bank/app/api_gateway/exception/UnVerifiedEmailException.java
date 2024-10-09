package com.bank.app.api_gateway.exception;

public class UnVerifiedEmailException extends RuntimeException {
    public UnVerifiedEmailException(String emailIdNotVerify) {
        super(emailIdNotVerify);
    }
}
