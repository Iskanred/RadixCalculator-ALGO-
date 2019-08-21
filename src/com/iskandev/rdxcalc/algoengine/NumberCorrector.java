package com.iskandev.rdxcalc.algoengine;

import com.iskandev.rdxcalc.exceptions.TooLargeNumberException;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

final class NumberCorrector {

    @NotNull
    static Number getCheckedIfTooLargeNumber(@NotNull final Number number) throws TooLargeNumberException {

        // If absolute value of a Number more than 'ABSOLUTE_MAX_DECIMAL_VALUE'
        if (number.abs().compareTo(Number.ABSOLUTE_MAX_DECIMAL_VALUE) > 0)
            throw new TooLargeNumberException();

        return number;
    }

    static void checkNumberSignum(final int signum) {
        if (signum < -1 || signum > 1)
            throw new IllegalArgumentException("Signum out of range.");
    }

    static void checkNumberRadix(final int radix) {
        if (radix < Number.MIN_RADIX|| radix > Number.MAX_RADIX)
            throw new IllegalArgumentException("Radix out of range.");
    }

    private static void checkNumberRepresentation(@NotNull final String stringRepresent, final int radix) {

        if (stringRepresent.isEmpty())
            throw new NumberFormatException("Zero length Number.");

        if (stringRepresent.equals("-"))
            throw new NumberFormatException("\"-\" is not Number.");

        if (!Pattern.matches("^-?[A-Z\\d]*\\.?[A-Z\\d]*$", stringRepresent))
            throw new NumberFormatException("\"" + stringRepresent + "\" is incorrect.");

        // Check if at least (one digit of a number) >= Number's radix
        for (int i = 0; i < stringRepresent.length(); i++) {
            /*
            ignore if it's a minus('-') or a decimal point('.')  symbol;
            compare with Number's radix if it's a digit (letters are digits too)
             */
            if (stringRepresent.charAt(i) != '-' && stringRepresent.charAt(i) != '.' &&
                    Character.getNumericValue(stringRepresent.charAt(i)) >= radix)
                throw new NumberFormatException("For input string: \"" + stringRepresent + "\" under radix \"" + radix + "\".");
        }
    }

    @NotNull
    static String getCleanedNumberRepresentation(@NotNull final String unsignedStringRepresent, final int radix) {

        checkNumberRepresentation(unsignedStringRepresent, radix);

        final int DECPOINT_INDEX = unsignedStringRepresent.indexOf(".");
        final StringBuilder correctableRepresent = new StringBuilder(unsignedStringRepresent);

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
            return "0";
        }

        if (correctableRepresent.charAt(0) == '.')
            correctableRepresent.insert(0, '0');

        return correctableRepresent.toString();
    }

    @NotNull
    static String[] getNumberPartsRepresents(@NotNull final String unsignedRepresent) {

        final String[] parts = unsignedRepresent.split("\\.");

        return (parts.length == 2) ? (new String[] {parts[0], parts[1]}) : (new String[] {parts[0], ""});
    }
}
