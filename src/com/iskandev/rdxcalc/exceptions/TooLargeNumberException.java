package com.iskandev.rdxcalc.exceptions;

/**
 * Class {@code TooLargeNumberException} is the Exception class
 *
 * If a number is too large to exist in the program
 * {@link com.iskandev.rdxcalc.algoengine.Number}-class objects throw the
 * {@code TooLargeNumberException}-class exceptions
 *
 * @see com.iskandev.rdxcalc.exceptions.NumberException
 */
public final class TooLargeNumberException extends NumberException {

    /**
     * Constructor that overrides {@link NumberException#message}
     */
    public TooLargeNumberException() {
        super.message = "TOO LARGE";
    }
}