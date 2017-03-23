package com.gargolin.passgen;

import java.sql.SQLOutput;
import java.util.Random;

/**
 * Created by User on 05.02.2017.
 */

public class Password {
    final static String smallLatinSet = "abcdefghijklmnopqrstuvwxyz";
    final static String bigLatinSet = smallLatinSet.toUpperCase();
    final static String numbersSet = "1234567890";

    private Random rand = new Random();
    private String password = "Password wasn't created due to enternal error";
    private char[] chars;
    private int smallCase;
    private int bigCase;
    private int digits;
    private String requiredSymbols;
    private String possibleSymbols;
    private boolean smallLatin;
    private boolean bigLatin;
    private boolean numbers;
    private int length;


    Password(boolean numbers, boolean bigLatin, boolean smallLatin, int digits, int bigCase, int smallCase,
             String requiredSymbols, String possibleSymbols, int length) {
        this.smallCase = smallCase;
        this.bigCase = bigCase;
        this.digits = digits;
        this.requiredSymbols = requiredSymbols;
        this.possibleSymbols = possibleSymbols;
        System.out.println(requiredSymbols);
        System.out.println(possibleSymbols);
        this.smallLatin = smallLatin;
        this.bigLatin = bigLatin;
        this.numbers = numbers;
        this.length = length;
        chars = new char[length + smallCase + bigCase + digits + requiredSymbols.length()];

    }

    private void filling(String dictionary, int j) {
        int i = 0;
        while (i < j) {
            char ch;
            if (dictionary.length() == j) ch = dictionary.charAt(i);
            else ch = dictionary.charAt(rand.nextInt(dictionary.length()));
            int k = rand.nextInt(length + smallCase + bigCase + digits + requiredSymbols.length());
            if (chars[k] == 0) {
                chars[k] = ch;
                i++;
            }
        }
    }

    public String creation() {
        filling(requiredSymbols, requiredSymbols.length());
        if (smallLatin) filling(smallLatinSet, smallCase);
        if (bigLatin) filling(bigLatinSet, bigCase);
        if (numbers) filling(numbersSet, digits);
        StringBuilder bigDictionary = new StringBuilder(possibleSymbols);
        if (smallLatin) bigDictionary.append(smallLatinSet);
        if (bigLatin) bigDictionary.append(bigLatinSet);
        if (numbers) bigDictionary.append(numbersSet);
        filling(bigDictionary.toString(), length);
        return password = new String(chars);
    }

    @Override
    public String toString() {
        return password;
    }


}
