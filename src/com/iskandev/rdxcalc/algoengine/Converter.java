package com.iskandev.rdxcalc.algoengine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

final class Converter {

    private final Number resultNumber;

    @Deprecated
    static final int MAX_ROUNDING_AMOUNT = 10;


    Converter(final Number convertNumber, final int resultRadix) {

        // Used for the fast-conversion
        int exponentRadix = logInt(Math.min(convertNumber.getRadix(), resultRadix), Math.min(convertNumber.getRadix(), resultRadix));

        if (convertNumber.getRadix() == resultRadix || convertNumber.getUnsignedRepresent().toString().equals("0") ||
                convertNumber.getUnsignedRepresent().toString().equals("1"))
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

    static char getDigitRadixRepresent(final int num) {
        if (num >= 2 && num <= 36)
            return (char)(num <= 9 ? num + 48 : num + 55);
        else
            throw new IllegalArgumentException("Impossible to get digit from number-argument! Number is more than 36 or less than 2.");
    }

    @Deprecated
    private Number getFastConversion (final Number number, final int resultRadix, final int exponentRadix) {
        if (number.getRadix() < resultRadix) {

        }
        else {

        }
        return getConversionFromDecimal(getConversionToDecimal(number), resultRadix);
    }

    private Number getConversionToDecimal(final Number number) {
        BigDecimal result = BigDecimal.valueOf(0).setScale(MAX_ROUNDING_AMOUNT, RoundingMode.HALF_UP);

        // Each digit will be raised to the power of this exponent (The max-exponent = integer_part.length - 1)
        int exp = number.getIntegerPartRepresent().length() - 1;

        for (int i = 0; i < number.getUnsignedRepresent().length(); i++) {
            if (number.getUnsignedRepresent().charAt(i) != '.') {
                final int digit = Character.getNumericValue(number.getUnsignedRepresent().charAt(i)); // letters have normal conversion
                final BigDecimal resultDigit = BigDecimal.valueOf(digit * getIntPower(number.getRadix(), exp))
                        .setScale(MAX_ROUNDING_AMOUNT, RoundingMode.HALF_UP);

                result = result.add(resultDigit); // convert every digit in decimal numeral-system and round it
                exp--;
            }
        }

        return new Number(10, result.toPlainString(), number.getSignum());
    }

    private Number getConversionFromDecimal(final Number number, final int resultRadix) {

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

        return new Number(resultRadix, resultStr.toString(), number.getSignum());
    }

    private int logInt(final int num1, final int num2) {
        for (int i = 2; i <= 5; i++)
            if (getIntPower(num1, i) == num2)
                return i;
        return -1; // an integer logarithm doesn't exist
    }

    private int getIntPower(final int number, final int exponent) {
        int result = number; // the number to the power one

        for (int i = 1; i < exponent; i++) // the min power is two
            result *= number;
        return result;
    }

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