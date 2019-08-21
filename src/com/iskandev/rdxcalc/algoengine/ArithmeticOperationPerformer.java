package com.iskandev.rdxcalc.algoengine;

import org.jetbrains.annotations.NotNull;

final class ArithmeticOperationPerformer {

    private final static char EMPTY_SYMBOL = ' ';

    /**
     * @deprecated useless unused constructor
     * {@code ArithmeticOperationPerformer}-class doesn't require to create an instance to perform arithmetic operations
     *
     * And it requires to use public static methods instead
     *
     * @see #getSum(Number, Number)
     * @see #getDifference(Number, Number)
     * @see #getProduct(Number, Number)
     * @see #getQuotient(Number, Number)
     */
    private ArithmeticOperationPerformer() {}

    @NotNull
    private static String[] getSameLengthNumbers(@NotNull final Number number1, @NotNull final Number number2) {
        final StringBuilder num1Int = new StringBuilder(number1.getIntegerPartRepresent()),
                num1Fract = new StringBuilder(number1.getFractionalPartRepresent());
        final StringBuilder num2Int = new StringBuilder(number2.getIntegerPartRepresent()),
                num2Fract = new StringBuilder(number2.getFractionalPartRepresent());
        final String[] results = new String[2];

        // if at least one of the numbers has a fraction-part -> add a decimal point to the fraction beginning
        if (num1Fract.length() != 0 || num2Fract.length() != 0) {
            num1Fract.insert(0, '.');
            num2Fract.insert(0, '.');
        }

        // Adding necessary space to fractional-part (to the end) for the long addition
        while (num1Fract.length() > num2Fract.length())
            num2Fract.append('0');
        while (num1Fract.length() < num2Fract.length())
            num1Fract.append('0');

        // Adding necessary space to integer-part (to the beginning)  the long addition
        while (num1Int.length() > num2Int.length())
            num2Int.insert(0, EMPTY_SYMBOL);
        while (num1Int.length() < num2Int.length())
            num1Int.insert(0, EMPTY_SYMBOL);

        results[0] = num1Int.toString() + num1Fract.toString();
        results[1] = num2Int.toString() + num2Fract.toString();

        return results;
    }

    @NotNull
    static Number getSum(@NotNull final Number number1, @NotNull final Number number2) {

        if (number1.getRadix() != number2.getRadix())
            throw new IllegalArgumentException("Radixes aren't equal");

        // If at least one of the number equals 0
        if (number1.equals(Number.ZERO))
            return number2;
        if (number2.equals(Number.ZERO))
            return number1;

        // If numbers have different not zero-signums
        if (number1.signum() != number2.signum())
            return getDifference(number1, number2.negate());

        // If numbers have the same not zero-signum
        else {

            // 'number2' has the same signum as 'number1', so the result will have the same as its
            final int RESULT_SIGNUM = number1.signum();
            final int RADIX = number1.getRadix(); // or 'number2.getRadix()

            // The same length numbers full unsigned representations
            final String[] numRepresents = getSameLengthNumbers(number1, number2);
            final String num1Str = numRepresents[0], num2Str = numRepresents[1];

            // Will be inverted at the right, then(before return) it will become corrected (at the left)
            final StringBuilder resultStr = new StringBuilder();

            /*
             To catch an overflow when sum of the digits more than max-digit in the numeral system
             So use it to add 1 to next digit and avoid an overflow
             */
            boolean adder = false;

            // Imitation of the long addition
            for (int i = num1Str.length() - 1; i >= 0; i--) {
                if (num1Str.charAt(i) == '.') {
                    resultStr.append('.');
                    continue;
                }

                final int digitOfNum1 = (num1Str.charAt(i) != EMPTY_SYMBOL) ?
                        Character.getNumericValue(num1Str.charAt(i)) : 0;
                final int digitOfNum2 = (num2Str.charAt(i) != EMPTY_SYMBOL) ?
                        Character.getNumericValue(num2Str.charAt(i)) : 0;

                int digitOfResult = digitOfNum1 + digitOfNum2;

                if (adder)
                    digitOfResult += 1;

                adder = (digitOfResult >= RADIX);

                if (adder)
                    digitOfResult -= RADIX;

                resultStr.append(Converter.forDigit(digitOfResult)); // result-number will start on the right
            }

            if (adder)
                resultStr.append('1');

            resultStr.reverse(); // invert and correct the number to read at the left

            return Number.valueOfUnsigned(RADIX, resultStr.toString(), RESULT_SIGNUM);
        }
    }

    @NotNull
    static Number getDifference(@NotNull final Number number1, @NotNull final Number number2)  {

        if (number1.getRadix() != number2.getRadix())
            throw new IllegalArgumentException("Radixes aren't equal");

        // If numbers are equal (and its signums too)
        if (number1.equals(number2))
            return Number.ZERO;

        // If at least one of the number equals 0
        if (number1.equals(Number.ZERO))
            return number2.negate();
        if (number2.equals(Number.ZERO))
            return number1;

        // If numbers have different not zero-signums
        else if (number1.signum() != number2.signum())
            return getSum(number1, number2.negate());

        // Else anyway numbers have the same not zero-signum and its aren't equal
        else {
            final int RADIX = number1.getRadix(); // or 'number2.getRadix()
            final int RESULT_SIGNUM;

            // The same length numbers full unsigned representations
            final String[] numRepresents = getSameLengthNumbers(number1, number2);
            final String maxNumStr, minNumStr;

            // Will be inverted at the right, then(before return) it will become corrected (at the left)
            final StringBuilder resultStr = new StringBuilder();

            /*
             To catch an overflow when difference of the digits less than 0
             So use it to subtract 1 from next digit and avoid an overflow
             */
            boolean taker = false;

            if (number1.abs().compareTo(number2.abs()) > 0) {
                RESULT_SIGNUM = number1.signum();

                maxNumStr = numRepresents[0];
                minNumStr = numRepresents[1];

            } else {
                RESULT_SIGNUM = -number1.signum();

                maxNumStr = numRepresents[1];
                minNumStr = numRepresents[0];
            }

            // Imitation of the long subtraction
            for (int i = maxNumStr.length() - 1; i >= 0; i--) {
                if (maxNumStr.charAt(i) == '.') {
                    resultStr.append('.');
                    continue;
                }

                int digitOfMaxNum = Character.getNumericValue(maxNumStr.charAt(i));
                final int digitOfMinNum = (minNumStr.charAt(i) != EMPTY_SYMBOL) ?
                        Character.getNumericValue(minNumStr.charAt(i)) : 0;
                final int digitOfResult;

                if (taker)
                    digitOfMaxNum--;

                taker = (digitOfMaxNum < digitOfMinNum);

                if (taker)
                    digitOfResult = RADIX - (digitOfMinNum - digitOfMaxNum) ;
                else
                    digitOfResult = digitOfMaxNum - digitOfMinNum;

                resultStr.append(Converter.forDigit(digitOfResult));
            }

            resultStr.reverse(); // invert and correct the number to read at the left

            return Number.valueOfUnsigned(RADIX, resultStr.toString(), RESULT_SIGNUM);
        }

    }

    @NotNull
    static Number getProduct(@NotNull final Number number1, @NotNull final Number number2) {

        if (number1.getRadix() != number2.getRadix())
            throw new IllegalArgumentException("Radixes aren't equal");

        return null;
    }

    @NotNull
    static Number getQuotient(@NotNull final Number number1, @NotNull final Number number2) {

        if (number1.getRadix() != number2.getRadix())
            throw new IllegalArgumentException("Radixes aren't equal");

        return null;
    }
}