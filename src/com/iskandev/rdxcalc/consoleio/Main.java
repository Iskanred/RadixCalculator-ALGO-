package com.iskandev.rdxcalc.consoleio;

import com.iskandev.rdxcalc.algoengine.Number;
import com.iskandev.rdxcalc.exceptions.IncorrectNumberException;
import com.iskandev.rdxcalc.exceptions.TooLargeNumberException;

import java.util.Scanner;

public class Main {

    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {

        final String str1 = in.next();
        final int radix1 = in.nextInt();
        //final int resultRadix1 = in.nextInt();

        final String str2 = in.next();
        final int radix2 = in.nextInt();
        //final int resultRadix2 = in.nextInt();

        try {
            Number n1 = new Number(radix1, str1, 1).checkTooLarge();
            Number n2 = new Number(radix2, str2, 1).checkTooLarge();

            //System.out.println((n1.getSignum() == -1 ? "-" : "") + n1.getFullRepresent() + " " + n1.getRadix());
            //System.out.println((n2.getSignum() == -1 ? "-" : "") + n2.getFullRepresent() + " " + n2.getRadix());

            Number nRes = new Number(n1.add(n2));

            System.out.println((nRes.getSignum() == -1 ? "-" : "") + nRes.getFullRepresent() + " " + nRes.getRadix());

        } catch (IncorrectNumberException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (TooLargeNumberException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
