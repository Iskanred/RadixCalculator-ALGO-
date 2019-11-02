package com.iskandev.rdxcalc.enums;

/**
 * Enum {@code ArithmeticSign} stores only four objects
 *
 * There are the signs of the primitive mathematical arithmetic operations
 * (addition, subtraction, multiplication, division)
 */
public enum ArithmeticSign {

    /**
     * There are the signs of the primitive mathematical arithmetic operations
     *
     * {@code PLUS} stores the:
     * @value '+' symbol/character of char type
     *
     * {@code MINUS} stores the:
     * @value '-' symbol/character of char type
     *
     * {@code MULTI} stores the:
     * @value '×' symbol/character of char type
     *
     * {@code DIV} stores the:
     * @value '÷' symbol/character of char type
     */
     PLUS('+'), MINUS('-'), MULTI('×'), DIV('÷');

    /**
     * This is the field responsible for symbol/character of an arithmetic sign
     *
     * Getter: {@link ArithmeticSign#getChar()}
     */
    private char character;

    /**
     * Constructor which puts the symbol/character of the sign to ArithmeticSign-object
     * @param character symbol/character of the sign
     */
    ArithmeticSign(final char character) {
        this.character = character;
    }


    /* Getters */

    /**
     * Getter of the {@link ArithmeticSign#character}
     * @return symbol/character of an arithmetic sign
     */
    public char getChar() {
        return character;
    }

}
