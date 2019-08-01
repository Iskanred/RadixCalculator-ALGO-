package com.iskandev.rdxcalc.exceptions;

/**
 * Class {@code IncorrectNumberException} is the Exception class
 *
 * If a number's representation or other number's data are incorrect
 * {@link com.iskandev.rdxcalc.algoengine.Number}-class objects throw the
 * {@code IncorrectNumberException}-class exceptions
 *
 * @see NumberException
 */
public final class IncorrectNumberException extends NumberException {

    /**
     * Constructor that overrides {@link NumberException#message}
     */
    public IncorrectNumberException() {
        super.message = "INCORRECT";
    }
}
