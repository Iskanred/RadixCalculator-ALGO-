package com.iskandev.rdxcalc.algoengine;

import com.iskandev.rdxcalc.exceptions.TooLargeNumberException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.regex.Pattern;


public final class Number implements Comparable<Number>{

    private final int radix;

    private final String unsignedRepresent, signedMinusRepresent;

    private final String integerPartRepresent, fractionalPartRepresent;

    private int signum;


    public Number(final int radix, final String stringRepresent) throws TooLargeNumberException {
        this(radix, stringRepresent, 1);
        checkTooLarge();
    }

    Number(final int radix, final String stringRepresent, final int signum) {

        this.signum = signum;
        this.radix = checkRadixCorrectness(radix);
        this.unsignedRepresent = getCorrectedRepresent(stringRepresent); // also can make 'signum'-fields negative(-1)

        // If it's equal ZERO - it has zero signum
        if (unsignedRepresent.equals("0"))
            this.signum = 0;

        // If it has negative-sign - signed-repesent has minus
        if (this.signum == -1)
            this.signedMinusRepresent = "-" + this.unsignedRepresent;
        else
            this.signedMinusRepresent = this.unsignedRepresent;

        final int DECPOINT_INDEX = unsignedRepresent.indexOf(".");

        if (DECPOINT_INDEX != -1) { // if number has a fractional-part
            integerPartRepresent = unsignedRepresent.substring(0, DECPOINT_INDEX);
            fractionalPartRepresent = unsignedRepresent.substring(DECPOINT_INDEX + 1);
        } else {
            integerPartRepresent = unsignedRepresent;
            fractionalPartRepresent = "";
        }
    }

    Number(@NotNull final Number number) {
        this.radix = number.getRadix();
        this.unsignedRepresent = number.getUnsignedRepresent();
        this.signedMinusRepresent = number.getSignedMinusRepresent();
        this.integerPartRepresent = number.getIntegerPartRepresent();
        this.fractionalPartRepresent = number.getFractionalPartRepresent();
        this.signum = number.getSignum();
    }

    public Number convertTo(final int radix) {
        return new Converter(this, checkRadixCorrectness(radix)).getResultNumber();
    }

    public Number add(@NotNull final Number addendNumber) throws TooLargeNumberException {
        // To convert both of numbers to the same numeral-system
        return new ArithmeticOperationPerformer(this, addendNumber).getSum().checkTooLarge();
    }

    public Number subtract(@NotNull final Number subtrahendNumber) throws TooLargeNumberException {
        // To convert both of numbers to the same numeral-system
        return new ArithmeticOperationPerformer(this, subtrahendNumber).getDifference().checkTooLarge();
    }

    public Number multiply(@NotNull final Number multiplicandNumber) throws TooLargeNumberException {
        // To convert both of numbers to the same numeral-system
        return new ArithmeticOperationPerformer(this, multiplicandNumber).getProduct().checkTooLarge();
    }

    public Number divide(@NotNull final Number divisorNumber) throws TooLargeNumberException {
        // To convert both of numbers to the same numeral-system
        return new ArithmeticOperationPerformer(this, divisorNumber).getQuotient().checkTooLarge();
    }

    @Override
    public int compareTo(@NotNull final Number comparableNumber) {

        // If numbers have different signums or both have zero-signum
        if ((signum == 0 && comparableNumber.getSignum() == 0) || signum != comparableNumber.getSignum())
            return Integer.compare(signum, comparableNumber.getSignum());

        // Else anyway numbers have the same not_zero-signum
        else {
            final BigDecimal thisNumDec = new BigDecimal(this.convertTo(10).getSignedMinusRepresent());
            final BigDecimal compNumDec = new BigDecimal(comparableNumber.convertTo(10).getSignedMinusRepresent());

            return thisNumDec.compareTo(compNumDec);
        }
    }

    Number negate() {
        return new Number(radix, unsignedRepresent, -signum);
    }

    private Number abs() {
        return (signum >= 0 ? this : this.negate());
    }

    private int checkRadixCorrectness (final int radix) {
        if (radix >= 2 && radix <= 36)
            return radix;
        else
            throw new IllegalArgumentException("Radix of number is incorrect.");
    }

    private String getCorrectedRepresent(@Nullable final String initStringRepresent) {

        final StringBuilder correctableRepresent = checkRepresentationCorrectness(initStringRepresent);

        // If number has minus at the beginning - it has negative signum, delete minus
        if (correctableRepresent.charAt(0) == '-') {
            this.signum = -1;
            correctableRepresent.deleteCharAt(0);
        }

        return getWithoutInsignificantSymbols(correctableRepresent).toString();
    }

    private StringBuilder checkRepresentationCorrectness (@Nullable final String stringRepresent) {

        if (stringRepresent == null)
            throw new NullPointerException("Number's string representation is null.");

        else if (stringRepresent.isEmpty())
            throw new NumberFormatException("Number's string representation is empty.");

        else {

            if (stringRepresent.equals("-"))
                throw new NumberFormatException("\"-\" is incorrect representation of number.");

            // Check if a representation of a number matches the regex-pattern
            if (!Pattern.matches("^-?[A-Z\\d]*\\.?[A-Z\\d]*$", stringRepresent))
                throw new NumberFormatException("\"" + stringRepresent + "\" contains incorrect characters.");

            // Check if at least (one digit of a number) >= number's radix
            for (int i = 0; i < stringRepresent.length(); i++) {
                /*
                ignore if it's a decimal point('.') or minus('-') symbol;
                compare with number's radix if it's a digit or a letter symbol
                 */
                if (stringRepresent.charAt(i) != '-' && stringRepresent.charAt(i) != '.' &&
                        Character.getNumericValue(stringRepresent.charAt(i)) >= radix)
                    throw new NumberFormatException("\"" + stringRepresent + "\" contains digits which is more than radix of number.");
            }

            return new StringBuilder(stringRepresent);
        }
    }

    private StringBuilder getWithoutInsignificantSymbols(@NotNull final StringBuilder stringRepresent) {

        final int DECPOINT_INDEX = stringRepresent.indexOf(".");
        final StringBuilder correctableRepresent = new StringBuilder(stringRepresent);

        // Clearing the trailing zeros and insignificant point at the end of a number's representation
        if (DECPOINT_INDEX != -1) {

            while (correctableRepresent.charAt(correctableRepresent.length() - 1) == '0')
                correctableRepresent.deleteCharAt(correctableRepresent.length() - 1);

            if (correctableRepresent.length() - 1 == DECPOINT_INDEX)
                correctableRepresent.deleteCharAt(correctableRepresent.length() - 1);
        }

        // Clearing the leading zeros and insignificant point at the beginning of a number's representation
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

    private Number checkTooLarge() throws TooLargeNumberException {
        final Number MAX_NUMBER = new Number(10, "9999999999999999999999999.9999999999", 1);

        // If positive representation of a Number more than MAX_NUMBER
        if (this.abs().compareTo(MAX_NUMBER) > 0)
            throw new TooLargeNumberException();

        return this;
    }

    /* Getters */

    public int getRadix() {
        return radix;
    }

    public String getSignedMinusRepresent() {
        return signedMinusRepresent;
    }

    int getSignum() {
        return signum;
    }

    String getUnsignedRepresent() {
        return unsignedRepresent;
    }

    String getIntegerPartRepresent() {
        return integerPartRepresent;
    }

    String getFractionalPartRepresent() {
        return fractionalPartRepresent;
    }
}
