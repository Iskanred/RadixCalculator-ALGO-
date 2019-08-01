package com.iskandev.rdxcalc.algoengine;

import com.iskandev.rdxcalc.enums.ArithmeticSign;
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
     * This is the signum of the number: -1 for negative, 0 for zero, 1 for positive
     *
     * Gets value from the constructors:
     * {@link Number#Number(int, String, int)}
     * {@link Number#Number(Number)}
     *
     * Getting access to this field from the outside provides getter:
     * {@link Number#getSignum()} ()}
     */
    private final int signum;

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

        /*
        This var is necessary to correct an input string-representation
        and then assign a right value to 'fullRepresent'-field
        */
        StringBuilder tmpFullRepresent;

        /*
        This var is necessary to make a right choice of the number's sign
        and then assign a right value to 'signum'-field
        */
        int tmpSignum = signum;

        try {
            this.radix = checkCorrectness(radix);
            tmpFullRepresent = checkCorrectness(stringRepresent);
        } catch (IllegalArgumentException e) {
            throw new IncorrectNumberException();
        }

        // If number has minus at the beginning - it has negative signum, delete minus
        if (tmpFullRepresent.charAt(0) == ArithmeticSign.MINUS.getChar()) {
            tmpSignum = -1;
            tmpFullRepresent.deleteCharAt(0);
        }

        fullRepresent = clearInsignificantZeros(tmpFullRepresent.indexOf("."), tmpFullRepresent);

        // But if it equals ZERO - it has zero signum
        if (fullRepresent.equals("0"))
            tmpSignum = 0;

        this.signum = tmpSignum; // assign right final value of number's signum to 'signum'-field

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
            return new Converter(this, checkCorrectness(radix)).getResultNumber();
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
            return new ArithmeticOperation(this, addendNumber, ArithmeticSign.PLUS).getResultNumber();
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
            return new ArithmeticOperation(this, subtrahendNumber, ArithmeticSign.MINUS).getResultNumber();
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
            return new ArithmeticOperation(this, multiplicandNumber, ArithmeticSign.MULTI).getResultNumber();
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
            return new ArithmeticOperation(this, divisorNumber, ArithmeticSign.DIV).getResultNumber();
        } catch (IllegalArgumentException e) {
            throw new IncorrectNumberException();
        }
    }

    /*
    @Override
    public int compareTo(final Number comparableNumber) throws IllegalArgumentException {
        final int LESS = -1, MORE = 1;

        if (!this.isNegative() && comparableNumber.isNegative())
            return MORE;
        if (this.isNegative() && !comparableNumber.isNegative())
            return LESS;
        // else anyway this Number and the 'comparableNumber' have the same 'negative'
        if (this.getIntegerPartRepresent().length() > comparableNumber.getIntegerPartRepresent().length()) {
            if (!this.isNegative())
                return MORE;
            else
                return LESS;
        }
        else if (this.getIntegerPartRepresent().length() < comparableNumber.getIntegerPartRepresent().length()) {
            if (!this.isNegative())
                return LESS;
            else
                return MORE;
        } else {
            try {
                BigDecimal thisNum10;
                BigDecimal compNum10;

                if (this.isNegative()) { // in this way 'comparableNumber' is negative too
                    thisNum10 = new BigDecimal(ArithmeticSign.MINUS.getString() + this.convertTo(10).getFullRepresent().toString());
                    compNum10 = new BigDecimal(ArithmeticSign.MINUS.getString() + comparableNumber.convertTo(10).getFullRepresent().toString());
                } else {
                    thisNum10 = new BigDecimal(this.convertTo(10).getFullRepresent().toString());
                    compNum10 = new BigDecimal(comparableNumber.convertTo(10).getFullRepresent().toString());
                }

                return thisNum10.compareTo(compNum10); // 1, 0, -1

            } catch (IncorrectNumberException e) {
                e.printStackTrace();
                throw new IllegalArgumentException();
            }
        }
    }
     */

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
     * @param radix is the radix of the numeral-system
     * @return just 'radix'-@param value; if it is correct
     * @throws IllegalArgumentException if radix is incorrect
     */
    private int checkCorrectness (final int radix) throws IllegalArgumentException {
        if (radix < 2 || radix > 36)
            throw new IllegalArgumentException();
        else
            return radix;
    }

    /**
     * This method checks string-representation of the number correctness
     *
     * @param stringRepresent is string-representation of the number
     * @return new StringBuilder object which representation is the 'str'-@param's representation; if it's correct
     * @throws IllegalArgumentException if it's not correct
     */
    private StringBuilder checkCorrectness (final String stringRepresent) throws IllegalArgumentException {
        if (stringRepresent != null && !stringRepresent.isEmpty()) {

            // Check if the string-representation of the number matches the regex-pattern
            if (!Pattern.compile("-?[A-Z\\d]*.?[A-Z\\d]*").matcher(stringRepresent).matches())
                throw new IllegalArgumentException("regex");

            // Check if at least (one digit of the number) >= number's radix
            for (int i = 0; i < stringRepresent.length(); i++) {
                /*
                ignore if it's a dot('.') or minus('-') symbol;
                compare with number's radix if it's a digit or a letter symbol
                 */
                if (stringRepresent.charAt(i) != ArithmeticSign.MINUS.getChar() && stringRepresent.charAt(i) != '.' &&
                        Character.getNumericValue(stringRepresent.charAt(i)) >= radix)
                    throw new IllegalArgumentException();
            }

            return new StringBuilder(stringRepresent);
        }
        else // if input string-representation is empty
            throw new IllegalArgumentException();
    }

    /**
     * This method clears insignificant zeros at the beginning of the number's string-representation,
     * <b>in the integer part!</b>
     *
     * And clear trailing zeros at the end of the number's string-representation,
     * <b>in the fractional part!</b>
     *
     * @param dotIndex is index of dot in the full string-representation
     *                 can be either any positive integer value
     *                 or -1 if number doesn't have fractional-part (therefore doesn't have dot too)
     */
    private String clearInsignificantZeros(final int dotIndex, final StringBuilder stringRepresent) {

        StringBuilder tmpStringRepresent = new StringBuilder(stringRepresent);

        // Clearing at the end of the number's fractional-part
        if (dotIndex != -1) { // if number has the fractional-part
            while (tmpStringRepresent.charAt(tmpStringRepresent.length() - 1) == '0')
                tmpStringRepresent.deleteCharAt(tmpStringRepresent.length() - 1);

            if (tmpStringRepresent.length() - 1 == dotIndex)
                tmpStringRepresent.deleteCharAt(tmpStringRepresent.length() - 1);
        }

        // Clearing at the beginning of the number's integer-part
        while (tmpStringRepresent.charAt(0) == '0' && (tmpStringRepresent.length() > 0 && tmpStringRepresent.charAt(1) != '.'))
            tmpStringRepresent.deleteCharAt(0);
        
        if (tmpStringRepresent.charAt(0) == '.')
            tmpStringRepresent.insert(0, '0');
        
        if (tmpStringRepresent.toString().isEmpty())
            tmpStringRepresent.append('0');

        return tmpStringRepresent.toString();
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
