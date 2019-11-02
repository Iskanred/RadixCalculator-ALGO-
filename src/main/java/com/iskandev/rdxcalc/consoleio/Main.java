package com.iskandev.rdxcalc.consoleio;

import com.iskandev.rdxcalc.algoengine.Number;
//import com.iskandev.rdxcalc.enums.ArithmeticSign;
import com.iskandev.rdxcalc.exceptions.TooLargeNumberException;

import java.util.Scanner;

public class Main {

    private static Scanner in = new Scanner(System.in);

    public static void main(final String[] args) {

        try {

            final Number n1 = Number.valueOfSigned(in.nextInt(), in.next());

            final char op = in.next().charAt(0);

            final Number n2 = Number.valueOfSigned(in.nextInt(), in.next());

            final Number nRes;

            switch (op) {
                case '+' :
                    nRes = n1.add(n2);
                    break;
                case '-' :
                    nRes = n1.subtract(n2);
                    break;
                case '×' :
                    nRes = n1.multiply(n2);
                    break;
                case '÷' :
                    nRes = n1.divide(n2);
                    break;
                default:
                    nRes = Number.valueOfSigned(0, "");
            }

            System.out.println(n1 + " " + op + " " + n2 + " = " + nRes);

            System.out.println(nRes + " -> " + nRes.convertTo(in.nextInt()));

        } catch (NullPointerException | IllegalArgumentException | TooLargeNumberException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }

    }
}
