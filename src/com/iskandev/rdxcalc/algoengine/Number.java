package com.iskandev.rdxcalc.algoengine;

import com.iskandev.rdxcalc.exceptions.IncorrectNumberException;
import com.iskandev.rdxcalc.exceptions.TooLargeNumberException;

import java.util.regex.Pattern;

/**
 * Class {@code Number} is the key-class, which was created to store number and its specific properties
 * <b>Objects of {@code Number}-class class are Immutable!</b>
 */
public final class Number /* implements Comparable<Number> */ {

    /**
     * This is the radix of the numeral system in which the number are represented
     *
     * Gets value from the constructors:
     * {@link Number#Number(int, String, int)}
     * {@link Number#Number(Number)}
     *
     * Getting access to this field from the outside provides getter:
     * {@link Number#getRadix()}
     */
    private final int radix;

    /**
     * This is full normalized(without insignificant symbols) string-representation of the number
     *
     * {@code fullRepresent} is the full string-representation of number
     *
     * Gets value from the constructors:
     * {@link Number#Number(int, String, int)}
     * {@link Number#Number(Number)}
     *
     * Getting access to this field from the outside provides getter:
     * {@link Number#getFullRepresent()}
     */
    private final String fullRepresent;

    /**
     * These are not full normalized string-representations(without insignificant symbols) of the number
     *
     * {@code integerPartRepresent} is the string-representation only of number's integer-part
     *
     * {@code fractionalPartRepresent} is the string-representation only of number's fractional-part
     * if number doesn't have fractional-part, it will be empty StringBuilder object
     *
     * Both of these fields get value from the constructors:
     * {@link Number#Number(int, String, int)}
     * {@link Number#Number(Number)}
     *
     * Getting access to {@code integerPartRepresent}-field from the outside provides getter:
     * {@link Number#getIntegerPartRepresent()}
     *
     * Getting access to {@code fractionalPartRepresent}-field from the outside provides getter:
     * {@link Number#getFractionalPartRepresent()}
     */
    private final String integerPartRepresent, fractionalPartRepresent;

    /**
     * This is the signum of the number: -1 for negative-signed, 0 for zero-signed, 1 for positive-signed numbers
     *
     * Gets value from the constructors:
     * {@link Number#Number(int, String, int)}
     * {@link Number#Number(Number)}
     *
     * Getting access to this field from the outside provides getter:
     * {@link Number#getSignum()} ()}
     */
    private int signum;

    /**
     * Constructor that gets a number using its specific properties
     * Then it corrects number's representation if it's enough correct for use
     *
     * @param radix - is the number's base/radix of the numeral-system, which an object gets
     * @param stringRepresent - is the string-representation of the number, which an object gets
     * @param signum - is the var responsible for the signum of the number, which an object gets
     * @throws IncorrectNumberException if an object got the number that incorrect for use
     */
    public Number(final int radix, final String stringRepresent, final int signum) throws IncorrectNumberException {

        this.signum = signum;

        try {
            this.radix = checkRadixCorrectness(radix); // throws IllegalArgumentException if it's incorrect
            this.fullRepresent = getCorrectedRepresent(stringRepresent); // also can make 'signum'-fields negative(-1)
        } catch (IllegalArgumentException e) {
            throw new IncorrectNumberException();
        }

        // It it's equal ZERO - it has zero signum
        if (fullRepresent.equals("0"))
            this.signum = 0;

        final int DOT_INDEX = fullRepresent.indexOf(".");

        if (DOT_INDEX != -1) { // if number has a fractional-part
            integerPartRepresent = fullRepresent.substring(0, DOT_INDEX);
            fractionalPartRepresent = fullRepresent.substring(DOT_INDEX + 1);
        } else {
            integerPartRepresent = fullRepresent;
            fractionalPartRepresent = "";
        }
    }

    /**
     * Constructor which is use when a Number-object should be a clone of the other Number-object
     * It clones all of the object properties too
     *
     * @param number is the object of Number-class which it needs to clone to the new object of Number-class
     */
    public Number(final Number number) {
        this.radix = number.getRadix();
        this.fullRepresent = number.getFullRepresent();
        this.integerPartRepresent = number.getIntegerPartRepresent();
        this.fractionalPartRepresent = number.getFractionalPartRepresent();
        this.signum = number.getSignum();
    }

    /**
     * This method returns a new number,
     * which is a representation of the current number in the new numeral-system
     *
     * @param radix is the radix of the new numeral-system
     * @return new number, which represents of the current number in the new numeral-system
     * @throws IncorrectNumberException if an incorrect number appears during the converting
     *
     * To convert number used the class {@link Converter}
     */
    public Number convertTo(final int radix) throws IncorrectNumberException {
        try {
            return new Converter(this, checkRadixCorrectness(radix)).getResultNumber();
        } catch (IllegalArgumentException e) {
            throw new IncorrectNumberException();
        }
    }

    /**
     * This method returns a new number,
     * which is the result of the addition between the current number(the first addend) and the addend-number
     *
     * @param addendNumber is the addend-number
     * @return a new number, which is the sum between the current number and the addend-number
     * @throws IncorrectNumberException if an incorrect number appears during the operation
     *
     * To perform the operation used the class {@link ArithmeticOperation}
     */
    public Number add(final Number addendNumber) throws IncorrectNumberException {
        // To convert both of the numbers to the same numeral-system
        try {
            return new ArithmeticOperation(this, addendNumber).getSum();
        } catch (IllegalArgumentException e) {
            throw new IncorrectNumberException();
        }
    }

    /**
     * This method returns a new number,
     * which is the result of the subtraction between the current number(minuend) and the subtrahend-number
     *
     * @param subtrahendNumber is the subtrahend-number
     * @return a new number, which is the difference between the current number and the subtrahend-number
     * @throws IncorrectNumberException if an incorrect number appears during the operation
     *
     * To perform the operation used the class {@link ArithmeticOperation}
     */
    public Number subtract(final Number subtrahendNumber) throws IncorrectNumberException {
        // To convert both of the numbers to the same numeral-system
        try {
            return new ArithmeticOperation(this, subtrahendNumber).getDifference();
        } catch (IllegalArgumentException e) {
            throw new IncorrectNumberException();
        }
    }

    /**
     * This method returns a new number,
     * which is the result of the multiplication between the current number(the first multiplicand) and the multiplicand-number
     *
     * @param multiplicandNumber is the multiplicand-number
     * @return a new number, which is the product between the current number and the multiplicand-number
     * @throws IncorrectNumberException if an incorrect number appears during the operation
     *
     * To perform the operation used the class {@link ArithmeticOperation}
     */
    public Number multiply(final Number multiplicandNumber) throws IncorrectNumberException {
        // To convert both of the numbers to the same numeral-system
        try {
            return new ArithmeticOperation(this, multiplicandNumber).getProduct();
        } catch (IllegalArgumentException e) {
            throw new IncorrectNumberException();
        }
    }

    /**
     * This method returns a new number,
     * which is the result of the division between the current number(dividend) and the divisor-number
     *
     * @param divisorNumber is the divisor-number
     * @return a new number, which is the quotient between the current number and the divisor-number
     * @throws IncorrectNumberException if an incorrect number appears during the operation
     *
     * To perform the operation used the class {@link ArithmeticOperation}
     */
    public Number divide(final Number divisorNumber) throws IncorrectNumberException {
        // To convert both of the numbers to the same numeral-system
        try {
            return new ArithmeticOperation(this, divisorNumber).getQuotient();
        } catch (IllegalArgumentException e) {
            throw new IncorrectNumberException();
        }
    }

    /**
     * This method checks the current number if it is too large to exist in the program
     *
     * @return reference to the current Number-class object, with which we work; if it is not too large
     * @throws TooLargeNumberException if it is too large
     */
    public Number checkTooLarge() throws TooLargeNumberException {
        final int INTEGER_MAX_LENGTH = 25;

        if (this.getIntegerPartRepresent().length() > INTEGER_MAX_LENGTH)
            throw  new TooLargeNumberException();
        else
            return this;
    }

    /**
     * This method checks correctness of the radix
     *
     * @param initRadix is the radix of the numeral-system
     * @return just 'radix'-@param value; if it is correct
     * @throws IllegalArgumentException if radix is incorrect
     */
    private int checkRadixCorrectness (final int initRadix) throws IllegalArgumentException {
        if (initRadix < 2 || initRadix > 36)
            throw new IllegalArgumentException();
        else
            return initRadix;
    }

    /**
     * This method gets the initial string-representation of the number and returns the corrected one from it
     * It checks init correctness and clear insignificant symbols
     *
     * As well, this method can change the value of {@link Number#signum}-field to negative(-1)
     * if number has minus at the beginning
     *
     * It also uses {@link Number#checkCorrectnessOf(String)} and
     * {@link Number#getWithoutInsignificantSymbols(StringBuilder)} methods
     *
     * @param initStringRepresent is the initial string-representation of the number
     * @return corrected string-representation of the number
     */
    private String getCorrectedRepresent(final String initStringRepresent) throws IllegalArgumentException {

        // Throws IllegalArgumentException if it's incorrect
        StringBuilder correctableRepresent = checkCorrectnessOf(initStringRepresent);

        // If number has minus at the beginning - it has negative signum, delete minus
        if (correctableRepresent.charAt(0) == '-') {
            this.signum = -1;
            correctableRepresent.deleteCharAt(0);
        }

        return getWithoutInsignificantSymbols(correctableRepresent).toString();
    }

    /**
     * This method checks string-representation of the number correctness
     *
     * Used only in the method {@link Number#getCorrectedRepresent(String)}
     *
     * @param stringRepresent is the string-representation of the number
     * @return new StringBuilder object which representation is the 'str'-@param's; if it's correct
     * @throws IllegalArgumentException if it's not correct
     */
    private StringBuilder checkCorrectnessOf (final String stringRepresent) throws IllegalArgumentException {

        if (stringRepresent != null && !stringRepresent.isEmpty()) {

            if (stringRepresent.equals("-"))
                throw new IllegalArgumentException();

            // Check if the string-representation of the number matches the regex-pattern
            if (!Pattern.matches("^-?[A-Z\\d]*\\.?[A-Z\\d]*$", stringRepresent))
                throw new IllegalArgumentException("regex");

            // Check if at least (one digit of the number) >= number's radix
            for (int i = 0; i < stringRepresent.length(); i++) {
                /*
                ignore if it's a dot('.') or minus('-') symbol;
                compare with number's radix if it's a digit or a letter symbol
                 */
                if (stringRepresent.charAt(i) != '-' && stringRepresent.charAt(i) != '.' &&
                        Character.getNumericValue(stringRepresent.charAt(i)) >= radix)
                    throw new IllegalArgumentException();
            }

            return new StringBuilder(stringRepresent);
        }

        else // if input string-representation is empty
            throw new IllegalArgumentException();
    }

    /**
     * This method gets number's string-representation and returns <b>cleaned</b> number's string-representation
     * without insignificant symbols(insignificant zeros and dot)
     *
     * Used only in the method {@link Number#getCorrectedRepresent(String)}
     *
     * @param stringRepresent is the number's string-representation
     * @return cleaned number's string-representation
     */
    private StringBuilder getWithoutInsignificantSymbols(final StringBuilder stringRepresent) {

        final int DOT_INDEX = stringRepresent.indexOf(".");

        StringBuilder correctableRepresent = new StringBuilder(stringRepresent);

        // Clearing at the end of the number's fractional-part
        if (DOT_INDEX != -1) {

            while (correctableRepresent.charAt(correctableRepresent.length() - 1) == '0')
                correctableRepresent.deleteCharAt(correctableRepresent.length() - 1);

            if (correctableRepresent.length() - 1 == DOT_INDEX)
                correctableRepresent.deleteCharAt(correctableRepresent.length() - 1);
        }

        // Clearing at the beginning of the number's integer-part
        try {
            while (correctableRepresent.charAt(0) == '0' && correctableRepresent.charAt(1) != '.')
                correctableRepresent.deleteCharAt(0);
        } catch (StringIndexOutOfBoundsException e) {
            return new StringBuilder("0");
        }

        if (correctableRepresent.charAt(0) == '.')
            correctableRepresent.insert(0, '0');

        return correctableRepresent;
    }


    /* Getters */

    /**
     * Getter of the {@code radix}-field
     * @return The {@link Number#radix}
     */
    public int getRadix() {
        return radix;
    }

    /**
     * Getter of the {@code fullRepresent}-field
     * @return The {@link Number#fullRepresent}
     */
    public String getFullRepresent() {
        return fullRepresent;
    }

    /**
     * Getter of the {@code signum}-field
     * @return The {@link Number#signum}
     */

    public int getSignum() {
        return signum;
    }

    /**
     * Getter of the {@code integerPartRepresent}-field
     * @return The {@link Number#integerPartRepresent}
     */
    String getIntegerPartRepresent() {
        return integerPartRepresent;
    }

    /**
     * Getter of the {@code fractionalPartRepresent}-field
     * @return The {@link Number#fractionalPartRepresent}
     */
    String getFractionalPartRepresent() {
        return fractionalPartRepresent;
    }
}
