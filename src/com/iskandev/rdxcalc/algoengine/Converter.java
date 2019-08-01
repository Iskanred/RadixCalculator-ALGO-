package com.iskandev.rdxcalc.algoengine;

import com.iskandev.rdxcalc.exceptions.IncorrectNumberException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Class {@code Converter} is the instrument for conversion the <b>number</b>
 * in any <i>numeral-system<i/>
 *
 * Class works with {@link Number}-class objects
 *
 * <b>Objects of {@code Converter}-class class are Immutable!</b>
 */
final class Converter {

    /**
     * It is the number-result of the converting
     *
     * Gets value from the constructor:
     * {@link Converter#Converter(Number, int)}
     *
     * Getting access to this field from the outside provides getter:
     * {@link Converter#getResultNumber()}
     */
    private final Number resultNumber;

    /**
     * Needs to change when user change the amount of digits after a dot - make it this amount + 1
     * @deprecated
     */
    @Deprecated
    static final int MAX_ROUNDING_AMOUNT = 10;

    /**
     * Constructor which determines how to convert number and form the result
     *
     * @param convertNumber is the convertible number
     * @param resultRadix is the radix of the numeral-system
     *                    in which there's necessary to convert the convertible number value.
     *                    Always correct!
     * @throws IllegalArgumentException if an incorrect number appears during the converting
     */
    Converter(final Number convertNumber, final int resultRadix) throws IllegalArgumentException {

        // Used for the fast-conversion
        int exponentRadix = logInt(Math.min(convertNumber.getRadix(), resultRadix), Math.min(convertNumber.getRadix(), resultRadix));

        if (convertNumber.getRadix() == resultRadix || convertNumber.getFullRepresent().toString().equals("0") ||
                convertNumber.getFullRepresent().toString().equals("1"))
            resultNumber = new Number(convertNumber);
        else if (exponentRadix != -1)
            resultNumber = getFastConversion(convertNumber, resultRadix, exponentRadix);
        else if (resultRadix == 10)
            resultNumber = getConversionToDecimal(convertNumber);
        else if (convertNumber.getRadix() == 10)
            resultNumber = getConversionFromDecimal(convertNumber, resultRadix);
        else
            resultNumber = getConversionFromDecimal(getConversionToDecimal(convertNumber), resultRadix);
    }

    /**
     * This method gets digit(which can be letter) of input number in the max numeral-system,
     * in which input-number can be exist in the only one-digit-representation (<i>radix-representation</i>)
     *
     * As well used in the class {@link ArithmeticOperation}
     *
     * @param num is just integer value
     *
     *          <b>Keep in mind, that 'num'-@param MUST be <i>(num >= 2 && num <= 36) </i>to correct work</i></b>
     *
     * @return digit, which is the <i>radix-representation</i> of the 'num'-@param
     * For ex. (num = 4, returns 4 num = 10, returns A, num = 36, return Z)
     */
    static char getDigitRadixRepresent(final int num) {
        return (char)(num <= 9 ? num + 48 : num + 55);
    }

    /**
     * This method is the specific method to convert numbers using
     * dyads(groups of two digits), triads(groups of three digits),
     * nibbles(groups of four digits), pentads(groups of five digits)
     * and show this process
     *
     * <b>Keep in mind any thing that may seem unnecessary in this method, most likely it is necessary to
     * show the complete solution of a fast converting to give the user an understanding of this operation</b>
     *
     * @param number is the convertible number
     * @param resultRadix is the radix of the numeral-system
     *                    in which there's necessary to convert the convertible number value
     * @param exponentRadix is the exponent that means how many times it's necessary
     *                      to rise the convertible number's radix to get the result radix
     *                      or
     *                      to rise the result radix to get the convertible number's radix
     * @return new number after converting 'number'-@param to the 'resultRadix'-@param
     * @@DEPRECATED - delete when will be used SQLite-method
     * @deprecated IncorrectNumberException
     */
    @Deprecated
    private Number getFastConversion (final Number number, final int resultRadix, final int exponentRadix) throws IllegalArgumentException {
        if (number.getRadix() < resultRadix) {

        }
        else {

        }
        return getConversionFromDecimal(getConversionToDecimal(number), resultRadix);
    }

    /**
     * This method converts a number from any numeral-system to the decimal numeral-system and show this process
     *
     * <b>Keep in mind any thing that may seem unnecessary in this method, most likely it is necessary to
     * show the complete solution of a converting to the decimal to give the user an understanding of this operation</b>
     *
     * @param number is the convertible number
     * @return new number after converting 'number'-@param to the 'resultRadix'-@param
     * @throws IllegalArgumentException the result-number turns out incorrect
     */
    private Number getConversionToDecimal(final Number number) throws IllegalArgumentException {
        BigDecimal result = BigDecimal.valueOf(0).setScale(MAX_ROUNDING_AMOUNT, RoundingMode.HALF_UP);

        // Each digit will be raised to the power of this exponent (The max-exponent = integer_part.length - 1)
        int exp = number.getIntegerPartRepresent().length() - 1;

        for (int i = 0; i < number.getFullRepresent().length(); i++) {
            if (number.getFullRepresent().charAt(i) != '.') {
                final int digit = Character.getNumericValue(number.getFullRepresent().charAt(i)); // letters have normal conversion
                final BigDecimal resultDigit = BigDecimal.valueOf(digit * getPowerInt(number.getRadix(), exp))
                        .setScale(MAX_ROUNDING_AMOUNT, RoundingMode.HALF_UP);

                result = result.add(resultDigit); // convert every digit in decimal numeral-system and round it
                exp--;
            }
        }

        try {
            return new Number(10, result.toPlainString(), number.getSignum());
        } catch (IncorrectNumberException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method converts a number in the decimal numeral-system to any another and show this process
     *
     * <b>Keep in mind, that a convertible number MUST be represented in the decimal number-system</b>
     *
     * <b>Also keep in mind any thing that may seem unnecessary in this method, most likely it is necessary to
     * show the complete solution of a converting from the decimal to give the user an understanding of this operation</b>
     *
     * @param number is the convertible number
     * @param resultRadix is the radix of the numeral-system
     *                    in which there's necessary to convert the convertible number value
     * @return new number after converting 'number'-@param to the 'resultRadix'-@param
     * @throws IllegalArgumentException the result-number turns out incorrect
     */
    private Number getConversionFromDecimal(final Number number, final int resultRadix) throws IllegalArgumentException {

        // Convert integer-part, and if number has no fractional-part return 'resultStr'
        final BigInteger bigIntRadix = new BigInteger(Integer.toString(resultRadix));
        BigInteger intPart = new BigInteger(number.getIntegerPartRepresent());
        StringBuilder resultStr = new StringBuilder();

        while (intPart.compareTo(BigInteger.ZERO) > 0) {
            final BigInteger divisionResult = intPart.divide(bigIntRadix);
            final BigInteger subtractionMinuend = divisionResult.multiply(bigIntRadix);
            final String subtractionResult = intPart.subtract(subtractionMinuend).toString();
            final char charDigit = getDigitRadixRepresent(Integer.parseInt(subtractionResult));

            resultStr.insert(0, charDigit);
            intPart = divisionResult;
        }

        // Convert fractional-part and add the result of converting to 'resultStr'
        if (number.getFractionalPartRepresent().length() != 0) {
            final BigDecimal bigDecRadix = new BigDecimal(bigIntRadix).setScale(MAX_ROUNDING_AMOUNT, RoundingMode.UNNECESSARY);
            BigDecimal decPart = new BigDecimal("0." + number.getFractionalPartRepresent()).
                    setScale(MAX_ROUNDING_AMOUNT, RoundingMode.HALF_UP);
            StringBuilder resultStrDec = new StringBuilder();

            while (resultStrDec.length() < MAX_ROUNDING_AMOUNT) {
                final BigDecimal multiplicationResult = decPart.multiply(bigDecRadix);
                final String intPartResult = multiplicationResult.toBigInteger().toString(); // getting integer-part
                final char charDigit = getDigitRadixRepresent(Integer.parseInt(intPartResult));
                final String decPartResult = getFractionalPartFrom(multiplicationResult); // getting fractional-part WITHOUT "0."-part

                resultStrDec.append(charDigit);
                decPart = new BigDecimal("0." + decPartResult); // getting fractional-part WITH "0."-part
            }
            resultStr.append('.').append(resultStrDec);
        }

        try {
            return new Number(resultRadix, resultStr.toString(), number.getSignum());
        } catch (IncorrectNumberException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method implements mathematical logarithm-function to base the first number of the second number, but
     * used <b>ONLY</b> to apply the fast conversion in the method: {@link Converter#getFastConversion(Number, int, int)}
     * and understand is it possible to apply this in the constructor: {@link Converter#Converter(Number, int)}
     *
     * So it can do <b>ONLY</b> this:
     * Checks exponents only from 2(because it makes no sense to consider less)
     * to 5(because even 2^6 is already more than any number in the interval 2-36 radixes)
     * Then if the first number to the power of exponent becomes equal the second number it return this exponent
     *
     * To get the power used special method: {@link Converter#getPowerInt(int, int)}
     *
     * @param num1 the first integer number, which will be raised
     * @param num2 the second integer number, which is the base of the second number
     * @return an integer logarithm of the first number for the base of the second number if that can be got
     * or -1 if that can't be got
     */
    private int logInt(final int num1, final int num2) {
        for (int i = 2; i <= 5; i++)
            if (getPowerInt(num1, i) == num2)
                return i;
        return -1; // an integer logarithm doesn't exist
    }

    /**
     * This method implements getting the mathematical power of the number
     *
     * It is intended <b>ONLY</b> for integers (number and exponent of type 'int'),
     * so it works faster than standard method: {@link Math#pow(double, double)}
     *
     * @param number is the number which it's necessary to raise to a power
     * @param exponent is the exponent used to get a power
     * @return the 'number'-@param to the power of the 'exponent'-@param
     */
    private int getPowerInt(final int number, final int exponent) {
        int result = number; // the number to the power one

        for (int i = 1; i < exponent; i++) // the min power is two
            result *= number;
        return result;
    }

    /**
     * This method gets fractional-part of the input-number
     *
     * @param bigFractional is just a decimal number
     * @return string-representation of the number's fractional-part
     */
    private String getFractionalPartFrom(final BigDecimal bigFractional) {
        /*
        Gets fractional-part with "0."-part using BigDecimal methods
        And delete it using StringBuilder,
        So get the fractional-part without "0."-part
         */
        return (new StringBuilder(bigFractional.remainder(BigDecimal.ONE).toPlainString()).delete(0, 2)).toString();
    }


    /* Getters */

    /**
     * Getter of the {@code resultNumber}-field
     * @return The {@link Converter#resultNumber}
     */
    Number getResultNumber() {
        return resultNumber;
    }
}