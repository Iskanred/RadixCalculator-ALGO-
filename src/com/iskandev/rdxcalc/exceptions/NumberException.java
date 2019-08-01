package com.iskandev.rdxcalc.exceptions;

/**
 * Class {@code NumberException} is the Exception class
 *
 * It is associated with the {@link com.iskandev.rdxcalc.algoengine.Number}-class objects
 * If number has wrong data
 * {@link com.iskandev.rdxcalc.algoengine.Number}-class objects throw the
 * {@code NumberException}-class exceptions
 *
 * {@code NumberException}-class objects not directly used elsewhere
 * But its are used to create new exception-classes that extend from this
 *
 * <i>Children:<i/>
 * @see IncorrectNumberException
 * @see TooLargeNumberException
 *
 * @see Exception
 */
class NumberException extends Exception {

    /**
     * The message of the exception
     *
     * Getting access to this field from the outside provides getter:
     * {@link NumberException#getMessage()}
     */
    String message = "NUMBER ERROR";

    /**
     * Getter of the {@code message}-field
     * @return The {@link NumberException#message}
     *
     * Overrides {@link Exception#getMessage()}
     */
    @Override
    public String getMessage() {
        return message;
    }
}
