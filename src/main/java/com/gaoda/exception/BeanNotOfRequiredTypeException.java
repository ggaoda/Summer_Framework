package com.gaoda.exception;

public class BeanNotOfRequiredTypeException extends BeansException{

    public BeanNotOfRequiredTypeException() {
    }

    public BeanNotOfRequiredTypeException(String message) {
        super(message);
    }
}
