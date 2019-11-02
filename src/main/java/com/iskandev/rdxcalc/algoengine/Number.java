package com.iskandev.rdxcalc.algoengine;

import com.iskandev.rdxcalc.exceptions.TooLargeNumberException;
import jdk.jfr.Unsigned;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;


@Immutable
public final class Number implements Comparable<Number> {

    static final int MIN_RADIX = 2;

    static final int MAX_RADIX = 36;


    static final Number ZERO = new Number("0", 0);

    static final Number NEGATIVE_ONE = new Number("1", -1);

    static final Number POSITIVE_ONE = new Number("1", 1);

    static final Number ABSOLUTE_MAX_DECIMAL_VALUE =
            new Number(10, "9999999999999999999999999.9999999999", 1);


    @Unsigned
    private final int radix;

    @Unsigned
    private final String unsignedRepresent;

    @Unsigned
    private final String integerPartRepresent, fractionalPartRepresent;

    private final int signum;


    @Contract
    private Number(final int radix, @NotNull final String unsignedRepresent, final int signum) {

        /*
        If a Number equals 0 or ±1 -> it must be assigned as
        ZERO or POSITIVE_ONE or NEGATIVE_ONE - already existed constant static instances
        And they mustn't be assigned as new instance which was created by this constructor
         */
        NumberCorrector.checkIfNumberEqualsZeroOrOne(unsignedRepresent, signum);

        this.radix = radix;
        this.unsignedRepresent = unsignedRepresent;
        this.signum = signum;

        String[] numberPartRepresents = NumberCorrector.getNumberPartsRepresents(unsignedRepresent);
        integerPartRepresent = numberPartRepresents[0];
        fractionalPartRepresent = numberPartRepresents[1];
    }

    @Contract
    private Number(@NotNull final String unsignedRepresent, final int signum) {

        this.radix = 10;
        this.unsignedRepresent = unsignedRepresent;
        this.signum = signum;

        String[] numberPartRepresents = NumberCorrector.getNumberPartsRepresents(unsignedRepresent);
        integerPartRepresent = numberPartRepresents[0];
        fractionalPartRepresent = numberPartRepresents[1];
    }


    @NotNull
    public static Number valueOfSigned(final int radix, @NotNull final String stringRepresent) throws TooLargeNumberException {

        NumberCorrector.checkNumberRadix(radix);

        final String correctedRepresent;
        final int signum;

        if (stringRepresent.charAt(0) == '-') {
            signum = -1;
            correctedRepresent = NumberCorrector.getCleanedNumberRepresentation(stringRepresent.substring(1), radix);
        } else {
            signum = 1;
            correctedRepresent = NumberCorrector.getCleanedNumberRepresentation(stringRepresent, radix);
        }

        if (correctedRepresent.equals("0"))
            return ZERO;

        if (correctedRepresent.equals("1"))
            return signum > 0 ? POSITIVE_ONE : NEGATIVE_ONE;

        return NumberCorrector.getCheckedIfTooLargeNumber(new Number(radix, correctedRepresent, signum));
    }

    @Contract
    @NotNull
    static Number valueOfUnsigned(final int radix, @NotNull final String unsignedStringRepresent, final int signum) {

        NumberCorrector.checkNumberSignum(signum);
        NumberCorrector.checkNumberRadix(radix);

        String correctedRepresent = NumberCorrector.getCleanedNumberRepresentation(unsignedStringRepresent, radix);

        if (correctedRepresent.equals("0"))
            return ZERO;

        if (correctedRepresent.equals("1"))
            return signum > 0 ? POSITIVE_ONE : NEGATIVE_ONE;

        return new Number(radix, correctedRepresent, signum);
    }


    @NotNull
    public Number convertTo(final int radix) {
        NumberCorrector.checkNumberRadix(radix);
        return Converter.getConversion(this, radix);
    }

    @NotNull
    public Number add(@NotNull final Number addendNumber) throws TooLargeNumberException {
        // To convert both of numbers to the same numeral-system
        return NumberCorrector.getCheckedIfTooLargeNumber(ArithmeticOperationPerformer.getSum(this, addendNumber));
    }

    @NotNull
    public Number subtract(@NotNull final Number subtrahendNumber) throws TooLargeNumberException {
        // To convert both of numbers to the same numeral-system
        return NumberCorrector.getCheckedIfTooLargeNumber(ArithmeticOperationPerformer.getDifference(this, subtrahendNumber));
    }

    @NotNull
    public Number multiply(@NotNull final Number multiplicandNumber) throws TooLargeNumberException {
        // To convert both of numbers to the same numeral-system
        return NumberCorrector.getCheckedIfTooLargeNumber(ArithmeticOperationPerformer.getProduct(this, multiplicandNumber));
    }

    @NotNull
    public Number divide(@NotNull final Number divisorNumber) throws TooLargeNumberException {
        // To convert both of numbers to the same numeral-system
        return NumberCorrector.getCheckedIfTooLargeNumber(ArithmeticOperationPerformer.getQuotient(this, divisorNumber));
    }


    /**
     * Don't use in the {@link Converter}-class if {@code obj} doesn't equal:
     * {@link Number#NEGATIVE_ONE}, {@link Number#ZERO}, {@link Number#POSITIVE_ONE}
     */
    @Override
    public boolean equals(@NotNull Object obj) {
        if (obj.getClass() != Number.class)
            return false;

        if ((obj == NEGATIVE_ONE && this != NEGATIVE_ONE) || (obj == ZERO && this != ZERO) || (obj == POSITIVE_ONE && this != POSITIVE_ONE))
            return false;

        return this.compareTo((Number) obj) == 0;
    }

    @Override
    public int compareTo(@NotNull final Number comparableNumber) {

        // If Numbers have the same instance (catches ZERO, POSITIVE-ONE, NEGATIVE-ONE)
        if (this == comparableNumber)
            return 0;

        // If Numbers have different signums
        if (signum != comparableNumber.signum)
            return Integer.compare(signum, comparableNumber.signum);

        // Else anyway numbers have the same not zero-signum
        else {
            final BigDecimal thisNumDec = new BigDecimal(this.convertTo(10).getRepresent());
            final BigDecimal compNumDec = new BigDecimal(comparableNumber.convertTo(10).getRepresent());

            return thisNumDec.compareTo(compNumDec);
        }
    }

    @Override
    public int hashCode() {
        return new BigDecimal(this.convertTo(10).getRepresent()).hashCode();
    }

    @Override
    @NotNull
    public String toString() {
        return getRepresent() + " (" + radix + ")";
    }


    @NotNull
    Number abs() {
        return (signum >= 0 ? this : this.negate());
    }

    @NotNull
    Number negate() {
        if (this.equals(NEGATIVE_ONE))
            return POSITIVE_ONE;

        if (this.equals(ZERO))
            return this;

        if (this.equals(POSITIVE_ONE))
            return NEGATIVE_ONE;

        return new Number(radix, unsignedRepresent, -signum);
    }

    /* Getters */

    int getRadix() {
        return radix;
    }

    @NotNull
    String getUnsignedRepresent() {
        return unsignedRepresent;
    }

    @NotNull
    private String getRepresent() {
        return (signum < 0) ? ("-" + unsignedRepresent) : (unsignedRepresent);
    }

    @NotNull
    String getIntegerPartRepresent() {
        return integerPartRepresent;
    }

    @NotNull
    String getFractionalPartRepresent() {
        return fractionalPartRepresent;
    }

    int signum() {
        return signum;
    }
}
