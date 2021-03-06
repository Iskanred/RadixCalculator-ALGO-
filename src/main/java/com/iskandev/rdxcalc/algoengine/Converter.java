package com.iskandev.rdxcalc.algoengine;

import jdk.jfr.Unsigned;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;


final class Converter {

    @Deprecated // FIXME: 8/19/2019
    @Unsigned
    static final int MAX_ROUNDING_AMOUNT = 10;

    /**
     * @deprecated useless unused constructor
     * {@code Converter}-class doesn't require to create an instance to perform a converting
     *
     * And it requires to use static method instead
     *
     * @see #getConversion(Number, int)
     */
    @Deprecated
    private Converter() {}

    static Number getConversion(@NotNull final Number convertibleNumber, final int resultRadix) {

        // Used for the fast-conversion
        final int EXPONENT_RADIX = log(Math.min(convertibleNumber.getRadix(), resultRadix),
                Math.max(convertibleNumber.getRadix(), resultRadix));

        if (convertibleNumber.getRadix() == resultRadix || convertibleNumber.equals(Number.ZERO) ||
                convertibleNumber.equals(Number.POSITIVE_ONE) || convertibleNumber.equals(Number.NEGATIVE_ONE))
            return convertibleNumber;
        else if (EXPONENT_RADIX != -1)
            return getFastConversion(convertibleNumber, resultRadix, EXPONENT_RADIX);
        else if (resultRadix == 10)
            return getConversionToDecimal(convertibleNumber);
        else if (convertibleNumber.getRadix() == 10)
            return getConversionFromDecimal(convertibleNumber, resultRadix);
        else
            return getConversionFromDecimal(getConversionToDecimal(convertibleNumber), resultRadix);
    }

    static char forDigit(final int digit) {
        return Character.toUpperCase(Character.forDigit(digit, Number.MAX_RADIX));
    }

    @Deprecated // FIXME: 8/19/2019
    @NotNull
    private static Number getFastConversion (@NotNull final Number number, final int resultRadix, final int exponent) {
        /*
        if (number.getRadix() < resultRadix) {

        }
        else {

        }
        */
        return getConversionFromDecimal(getConversionToDecimal(number), resultRadix);
    }

    @NotNull
    private static Number getConversionToDecimal(@NotNull final Number number) {
        BigDecimal result = BigDecimal.ZERO.setScale(MAX_ROUNDING_AMOUNT, RoundingMode.HALF_UP);

        // Each digit will be raised to the power of this exponent (The max-exponent = integer_part.length - 1)
        int exp = number.getIntegerPartRepresent().length() - 1;

        for (int i = 0; i < number.getUnsignedRepresent().length(); i++) {
            if (number.getUnsignedRepresent().charAt(i) != '.') {
                final int intDigit = Character.getNumericValue(number.getUnsignedRepresent().charAt(i));
                final BigDecimal resultDigit = BigDecimal.valueOf(intDigit * Math.pow(number.getRadix(), exp))
                        .setScale(MAX_ROUNDING_AMOUNT, RoundingMode.HALF_UP);

                result = result.add(resultDigit); // convert every digit in decimal numeral-system and round it
                exp--;
            }
        }

        return Number.valueOfUnsigned(10, result.toPlainString(), number.signum());
    }

    @NotNull
    private static Number getConversionFromDecimal(@NotNull final Number number, final int resultRadix) {

        // Convert integer-part, and if number has no fractional-part return 'resultStr'
        final BigInteger bigIntRadix = BigInteger.valueOf(resultRadix);
        BigInteger intPart = new BigInteger(number.getIntegerPartRepresent());
        final StringBuilder resultStr = new StringBuilder();

        while (intPart.compareTo(BigInteger.ZERO) > 0) {
            final BigInteger divisionResult = intPart.divide(bigIntRadix);
            final BigInteger subtractionMinuend = divisionResult.multiply(bigIntRadix);
            final String subtractionResult = intPart.subtract(subtractionMinuend).toString();
            final char charDigit = forDigit(Integer.parseInt(subtractionResult));

            resultStr.insert(0, charDigit);
            intPart = divisionResult;
        }

        // Convert fractional-part and add the result of it's converting to 'resultStr'
        if (number.getFractionalPartRepresent().length() != 0) {
            final BigDecimal bigFractRadix = new BigDecimal(bigIntRadix).setScale(MAX_ROUNDING_AMOUNT, RoundingMode.UNNECESSARY);
            BigDecimal fractPart = new BigDecimal("0." + number.getFractionalPartRepresent()).
                    setScale(MAX_ROUNDING_AMOUNT, RoundingMode.HALF_UP);
            final StringBuilder resultStrFract = new StringBuilder();

            while (resultStrFract.length() < MAX_ROUNDING_AMOUNT) {
                final BigDecimal multiplicationResult = fractPart.multiply(bigFractRadix); // using fractional-part WITH "0."-part
                final String intPartResult = multiplicationResult.toBigInteger().toString(); // getting integer-part
                final char charDigit = forDigit(Integer.parseInt(intPartResult));
                final String decPartResult = getFractionalPartFrom(multiplicationResult); // getting fractional-part WITHOUT "0."-part

                resultStrFract.append(charDigit);
                fractPart = new BigDecimal("0." + decPartResult); // getting fractional-part WITH "0."-part
            }
            resultStr.append('.').append(resultStrFract);
        }

        return Number.valueOfUnsigned(resultRadix, resultStr.toString(), number.signum());
    }

    private static int log(final int base, final int arg) {
        for (int e = 2; e <= 5; e++)
            if (Math.pow(base, e) == arg)
                return e;
        return -1; // an integer logarithm doesn't exist
    }

    @NotNull
    private static String getFractionalPartFrom(@NotNull final BigDecimal bigFractional) {
        /*
        Gets fractional-part with "0."-part using BigDecimal methods
        And delete it using StringBuilder,
        So get the fractional-part without "0."-part
         */
        return (new StringBuilder(bigFractional.remainder(BigDecimal.ONE).toPlainString()).delete(0, 2)).toString();
    }
}