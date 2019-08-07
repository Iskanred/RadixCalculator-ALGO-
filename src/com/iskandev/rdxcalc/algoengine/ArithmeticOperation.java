package com.iskandev.rdxcalc.algoengine;

final class ArithmeticOperation {

    private final int radix;

    private final Number number1, number2;

    private final static char EMPTY_SYMBOL = ' ';

    ArithmeticOperation(final Number number1, final Number number2) {

        if (number1.getRadix() != number2.getRadix())
            throw new IllegalArgumentException("Impossible to perform an operation! Radix of the first number doesn't equal radix of the second number.");

        this.radix = number1.getRadix(); // or number2.getRadix();
        this.number1 = number1;
        this.number2 = number2;
    }

    Number getSum() {

        // If at least one of the numbers equals 0
        if (number1.getSignum() == 0 )
            return new Number(number2);
        else if (number2.getSignum() == 0)
            return new Number(number1);

        if (number1.getSignum() > 0 && number2.getSignum() < 0)
            return getDifference();
        else if (number1.getSignum() < 0 && number2.getSignum() > 0)
            return getDifference();
        else { // if 'number1' and 'number2' have the same signum

            // 'number2' has the same signum as 'number1', so the result will have the same as its
            int signumOfResult = number1.getSignum();

            StringBuilder num1IntStr = new StringBuilder(number1.getIntegerPartRepresent()),
                    num2IntStr = new StringBuilder(number2.getIntegerPartRepresent());
            StringBuilder num1FractStr = new StringBuilder(number1.getFractionalPartRepresent()),
                    num2FractStr = new StringBuilder(number2.getFractionalPartRepresent());
            String num1FullStr, num2FullStr;

            // Will be inverted at the right, then(before return) it will become normalized (at the left)
            StringBuilder resultStr = new StringBuilder();

            /*
             To catch overflow when sum of the digits more than max-digit in the numeral system
             And add 1 to next digit
             */
            boolean adder = false;

            // if at least one of the numbers has a fraction-part - add a decimal point to the fraction beginning
            if (num1FractStr.length() != 0 || num2FractStr.length() != 0) {
                num1FractStr.insert(0, '.');
                num2FractStr.insert(0, '.');
            }

            // Adding necessary space to fractional-part (to the end) for the long addition
            while (num1FractStr.length() > num2FractStr.length())
                num2FractStr.append(EMPTY_SYMBOL);
            while (num1FractStr.length() < num2FractStr.length())
                num1FractStr.append(EMPTY_SYMBOL);

            // Adding necessary space to integer-part (to the beginning)  the long addition
            while (num1IntStr.length() > num2IntStr.length())
                num2IntStr.insert(0, EMPTY_SYMBOL);
            while (num1IntStr.length() < num2IntStr.length())
                num1IntStr.insert(0, EMPTY_SYMBOL);

            num1FullStr = num1IntStr.toString() + num1FractStr.toString();
            num2FullStr = num2IntStr.toString() + num2FractStr.toString();

            // Imitation of the long addition
            for (int i = num1FullStr.length() - 1; i >= 0; i--) {
                if (num1FullStr.charAt(i) == '.') {
                    resultStr.append('.');
                    continue;
                }

                int digitOfNum1 = (num1FullStr.charAt(i) != EMPTY_SYMBOL) ? Character.getNumericValue(num1FullStr.charAt(i)) : 0;
                int digitOfNum2 = (num2FullStr.charAt(i) != EMPTY_SYMBOL) ? Character.getNumericValue(num2FullStr.charAt(i)) : 0;
                int digitOfResult = digitOfNum1 + digitOfNum2;

                if (adder)
                    digitOfResult += 1;

                adder = (digitOfResult >= radix);

                if (adder)
                    digitOfResult -= radix;

                resultStr.append(Converter.getDigitRadixRepresent(digitOfResult)); // result-number will start on the right
            }

            if (adder)
                resultStr.append('1');

            resultStr.reverse(); // invert and normalize the number to read at the left

            return new Number(radix, resultStr.toString(), signumOfResult);
        }
    }

    Number getDifference()  {

       /*
        if(!number1.isNegative() && number2.isNegative() && from == FROM_MAIN)
            return new StringBuilder(sum(FROM_SUB));

        else if(number1.isNegative() && !number2.isNegative() && from == FROM_MAIN)
            return new StringBuilder(Character.toString(MainActivity.MINUS) + sum(FROM_SUB));

        else if(number1.isNegative() && number2.isNegative() && from == FROM_MAIN) {
            swapNumbers();
            return sub(FROM_SUB);
        }

        else {
            StringBuilder maxStr_dec, maxStr_int, minStr_dec, minStr_int, maxStr, minStr,
                    result = new StringBuilder();
            boolean negative = false;

            BigDecimal big1 = new BigDecimal(new Convertor(number1, 10).getResult().getStr().toString());
            BigDecimal big2 = new BigDecimal(new Convertor(number2, 10).getResult().getStr().toString());

            if (big1.compareTo(big2) == 1) {
                maxStr_int = number1.getIntegerPartStr();
                maxStr_dec = number1.getDecimalPartStr();
                minStr_int = number2.getIntegerPartStr();
                minStr_dec = number2.getDecimalPartStr();
            } else if (big1.compareTo(big2) == 0)
                return new StringBuilder("0");
            else {
                negative = true;
                maxStr_int = number2.getIntegerPartStr();
                maxStr_dec = number2.getDecimalPartStr();
                minStr_int = number1.getIntegerPartStr();
                minStr_dec = number1.getDecimalPartStr();
            }

            while (maxStr_dec.length() > minStr_dec.length())
                minStr_dec.append('0');
            while (maxStr_dec.length() < minStr_dec.length())
                maxStr_dec.append('0');

            maxStr = new StringBuilder(maxStr_int.toString() + '.' + maxStr_dec.toString());
            minStr = new StringBuilder(minStr_int.toString() + '.' + minStr_dec.toString());
            maxStr.reverse();
            minStr.reverse();

            int digitResult = 0;
            boolean taker = false;

            for (int i = 0; i < maxStr.length(); i++) {
                if (maxStr.charAt(i) != '.') {
                    int digit1 = getDigit(maxStr, i);
                    int digit2 = minStr.length() > i ? getDigit(minStr, i) : 0;

                    if (taker)
                        digit1--;

                    taker = (digit1 < digit2);

                    if (taker)
                        digitResult = CC - (digit2 - digit1);
                    else
                        digitResult = digit1 - digit2;

                    result.insert(0, getCharInCC(digitResult));
                } else
                    result.insert(0, '.');
            }

            if (negative)
                result.insert(0, MainActivity.MINUS);

            return result;
        }

        */

       return new Number(radix, "", 0);
    }

    Number getProduct() {
        return new Number(radix, "", 0);
    }

    Number getQuotient() {
        return new Number(radix, "", 0);
    }
}