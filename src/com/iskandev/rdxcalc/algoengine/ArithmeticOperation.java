package com.iskandev.rdxcalc.algoengine;

import com.iskandev.rdxcalc.enums.ArithmeticSign;
import com.iskandev.rdxcalc.exceptions.IncorrectNumberException;

/**
 * Class {@code ArithmeticOperation} is the tool for the performing
 * primitive mathematical <i>arithmetic operation</i>
 * (addition or subtraction or multiplication or division) with pair of <b>numbers</b>
 *
 * Class works with {@link Number}-class objects
 *
 * <b>Objects of {@code ArithmeticOperation}-class are Immutable!</b>
 */
final class ArithmeticOperation {

    /**
     * This is the number that is the result of an arithmetic operation
     *
     * Gets value from the constructor:
     * {@link ArithmeticOperation#ArithmeticOperation(Number, Number, ArithmeticSign)}
     *
     * Getting access to this field from the outside provides getter:
     * {@link ArithmeticOperation#getResultNumber()}
     */
    private final Number resultNumber;

    /**
     * This is the radix of the numeral-system in which both of numbers are represented for performing
     * an arithmetic operation
     *
     * Gets value from the constructor:
     * {@link ArithmeticOperation#ArithmeticOperation(Number, Number, ArithmeticSign)}
     */
    private final int radix;

    /**
     * This is necessary to fill empty space in the representation of long arithmetic operations
     * {@value} ' ' is the symbol that can be typed if press ALT+255 in Windows Operating System
     */
    private final static char EMPTY_SYMBOL = ' ';

    /**
     * Constructor which chooses which arithmetic operation needs to perform
     *
     * @param number1 is the first number to perform operation
     * @param number2 is the second number to perform operation
     *
     *                <b>Keep in mind that 'number1'- and 'number2'- @params
     *                MUST be represented in the same numeral-system</b>
     *
     * @param arithmeticSign is the sign which is used for operation, it is the {@link ArithmeticSign}-enum object
     * @throws IllegalArgumentException when incorrect numbers appears while performing an operation
     */
    ArithmeticOperation(final Number number1, final Number number2, final ArithmeticSign arithmeticSign) throws IllegalArgumentException {
        this.radix = number1.getRadix(); // or number2.getRadix();

        switch (arithmeticSign) {
            case PLUS:
                resultNumber = getSumOf(number1, number2);
                break;
            case MINUS:
                resultNumber = getDifferenceOf(number1, number2);
                break;
            case MULTI:
                resultNumber = getProductOf(number1, number2);
                break;
            case DIV:
                resultNumber = getQuotientOf(number1, number2);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * This method perform <b>long addition</b> between the two numbers
     * It performs the long addition like a human to show the complete solution of a getting the sum later
     *
     * <b>Keep in mind any thing that may seem unnecessary in this method, most likely it is necessary to
     * show the complete solution of a long addition to give the user an understanding of this operation</b>
     *
     * @param number1 is the first addend-number
     * @param number2 is the second addend-number
     * @return the sum of the numbers
     * @throws IllegalArgumentException if the result-number turns out incorrect
     */
    private Number getSumOf(final Number number1, final Number number2) throws IllegalArgumentException {

        // If at least one of the numbers equals 0
        if (number1.getSignum() == 0 )
            return new Number(number2);
        else if (number2.getSignum() == 0)
            return new Number(number1);

        if (number1.getSignum() > 0 && number2.getSignum() < 0)
            return getDifferenceOf(number1, number2);
        else if (number1.getSignum() < 0 && number2.getSignum() > 0)
            return getDifferenceOf(number2, number1);
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

            // if at least one of the numbers has a fraction-part - add a dot to the fraction beginning
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

            try {
                return new Number(radix, resultStr.toString(), signumOfResult);
            } catch (IncorrectNumberException e) {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * This method perform <b>long subtraction</b> between the two numbers
     * It performs the long subtraction like a human to show the complete solution of a getting the difference later
     *
     * <b>Keep in mind any thing that may seem unnecessary in this method, most likely it is necessary to
     * show the complete solution of a long subtraction to give the user an understanding of this operation</b>
     *
     * @param number1 is the minuend-number
     * @param number2 is the subtrahend-number
     * @return the difference of the numbers
     * //@throws IncorrectNumberException if the result-number turns out incorrect
     */
    private Number getDifferenceOf(final Number number1, final Number number2) throws IllegalArgumentException {

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
        try {
            return new Number(radix, "", 0);
        } catch (IncorrectNumberException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method perform <b>long multiplication</b> between the two numbers
     * It performs the long multiplication like a human to show the complete solution of a getting the product later
     *
     * <b>Keep in mind any thing that may seem unnecessary in this method, most likely it is necessary to
     * show the complete solution of a long multiplication to give the user an understanding of this operation</b>
     *
     * @param number1 is the multiplier-number
     * @param number2 is the multiplicand-number
     * @return the product of the numbers
     * //@throws IncorrectNumberException if the result-number turns out incorrect
     */
    private Number getProductOf(final Number number1, final Number number2) throws IllegalArgumentException {
        try {
            return new Number(radix, "", 0);
        } catch (IncorrectNumberException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method perform <b>long division</b> between the two numbers
     * It performs the long division like a human to show the complete solution of a getting the quotient later
     *
     * <b>Keep in mind any thing that may seem unnecessary in this method, most likely it is necessary to
     * show the complete solution of a long division to give the user an understanding of this operation</b>
     *
     * @param number1 is the dividend-number
     * @param number2 is the divisor-number
     * @return the quotient of the numbers
     * //@throws IncorrectNumberException if the result-number turns out incorrect
     */
    private Number getQuotientOf(final Number number1, final Number number2) throws IllegalArgumentException {
        try {
            return new Number(radix, "", 0);
        } catch (IncorrectNumberException e) {
            throw new IllegalArgumentException();
        }
    }


    /* Getters */

    /**
     * Getter of the {@code resultNumber}-field
     * @return {@link ArithmeticOperation#resultNumber}
     */
    Number getResultNumber() {
        return resultNumber;
    }
}