package com.iskandev.rdxcalc.algoengine;

import org.jetbrains.annotations.NotNull;

final class ArithmeticOperationPerformer {

    private final static char EMPTY_SYMBOL = ' ';


    private final int radix;

    private Number number1, number2;

    ArithmeticOperationPerformer(@NotNull final Number number1, @NotNull final Number number2) {

        if (number1.getRadix() != number2.getRadix())
            throw new IllegalArgumentException("Impossible to perform an operation! Radix of the first number doesn't equal radix of the second number.");

        this.radix = number1.getRadix(); // or number2.getRadix();
        this.number1 = number1;
        this.number2 = number2;
    }

    @NotNull
    private String[] getNormalizedNumbers () {
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
    Number getSum() {

        // If at least one of the number equals 0
        if (number1.getSignum() == 0)
            return new Number(number2);
        if (number2.getSignum() == 0)
            return new Number(number1);

        // If numbers have different not_zero-signums
        if (number1.getSignum() > 0 && number2.getSignum() < 0 || number1.getSignum() < 0 && number2.getSignum() > 0) {
            number2 = number2.negate();
            return getDifference();
        }

        // If numbers have the same not_zero-signum
        else {

            // 'number2' has the same signum as 'number1', so the result will have the same as its
            final int RESULT_SIGNUM = number1.getSignum();

            // Normalized numbers full unsigned representations
            final String[] numRepresents = getNormalizedNumbers();
            final String num1Str = numRepresents[0], num2Str = numRepresents[1];

            // Will be inverted at the right, then(before return) it will become normalized (at the left)
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

                adder = (digitOfResult >= radix);

                if (adder)
                    digitOfResult -= radix;

                resultStr.append(Converter.forDigit(digitOfResult)); // result-number will start on the right
            }

            if (adder)
                resultStr.append('1');

            resultStr.reverse(); // invert and normalize the number to read at the left

            return new Number(radix, resultStr.toString(), RESULT_SIGNUM);
        }
    }

    @NotNull
    Number getDifference()  {

        // If at least one of the number equals 0
        if (number1.getSignum() == 0)
            return new Number(number2.negate());
        if (number2.getSignum() == 0)
            return new Number(number1);

        // If numbers are equal (and its signums too)
        if (number1.compareTo(number2) == 0)
            return new Number(radix, "0", 0);

        // If numbers have different not_zero-signums
        else if (number1.getSignum() > 0 && number2.getSignum() < 0 || number1.getSignum() < 0 && number2.getSignum() > 0) {
            number2 = number2.negate();
            return getSum();
        }

        // If numbers have the same not_zero-signum and its are not equal
        else {
            final int RESULT_SIGNUM;

            // Normalized numbers full unsigned representations
            final String[] numRepresents = getNormalizedNumbers();
            final String maxNumStr, minNumStr;

            // Will be inverted at the right, then(before return) it will become normalized (at the left)
            final StringBuilder resultStr = new StringBuilder();

            /*
             To catch an overflow when difference of the digits less than 0
             So use it to subtract 1 from next digit and avoid an overflow
             */
            boolean taker = false;

            if (number1.abs().compareTo(number2.abs()) > 0) {
                RESULT_SIGNUM = number1.getSignum() > 0 ? 1 : -1;

                maxNumStr = numRepresents[0];
                minNumStr = numRepresents[1];

            } else {
                RESULT_SIGNUM = number1.getSignum() < 0 ? 1 : -1;

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
                    digitOfResult = radix - (digitOfMinNum - digitOfMaxNum) ;
                else
                    digitOfResult = digitOfMaxNum - digitOfMinNum;

                resultStr.append(Converter.forDigit(digitOfResult));
            }

            resultStr.reverse(); // invert and normalize the number to read at the left

            return new Number(radix, resultStr.toString(), RESULT_SIGNUM);
        }

    }

    @NotNull
    Number getProduct() {
        return null;
    }

    @NotNull
    Number getQuotient() {
        return null;
    }
}