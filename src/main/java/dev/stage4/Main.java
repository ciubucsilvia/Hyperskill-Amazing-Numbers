package dev.stage4;

import java.util.Scanner;

public class Main {
    public static final int BUZZ_NUMBER = 7;
    protected static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        welcome();
        dispayInstructions();

        while (true) {
            long number = askRequest();

            if(number == 0) {
                System.out.println("\nGoodbye!");
                break;
            }

            // Check if the number is natural
            if(isNatural(number)) {
                System.out.println(printProperties(number));
            } else {
                System.out.println("\nThe first parameter should be a natural number or zero.\n");
            }
        }
        scanner.close();
    }

    protected static void welcome() {
        System.out.println("Welcome to Amazing Numbers!");
        System.out.println();
    }

    protected static void dispayInstructions() {
        System.out.println("Supported requests:");
        System.out.println("- enter a natural number to know its properties;");
        System.out.println("- enter 0 to exit.");
        System.out.println();
    }

    // Ask a user to enter a natural number
    protected static long askRequest() {
        System.out.print("Enter a request: ");
        long number = 0;
        try {
            number = scanner.nextLong();

        } catch (Exception e) {
            System.out.println("Invalid input! Please enter a valid number.");
            scanner.next(); // Clear the invalid input
            return -1; // Return an invalid number to trigger error message
        }

        return number;
    }

    public static String printProperties(long number) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Properties of %d\n", number));
        sb.append(String.format("        even: %b\n", isEven(number)));
        sb.append(String.format("         odd: %b\n", !isEven(number)));
        sb.append(String.format("        buzz: %b\n", isBuzz(number)));
        sb.append(String.format("        duck: %b\n", isDuck(number)));
        sb.append(String.format(" palindromic: %b\n", isPalindromic(number)));

        return sb.toString();
    }

    public static boolean isNatural(long number) {
        return number >= 1;
    }

    public static String printParity(long number) {
        String parity = "Odd";
        if(isEven(number)) {
            parity = "Even";
        }

        return String.format("This number is %s.", parity);
    }

    public static boolean isEven(long number) {
        return number % 2 == 0;
    }

    /**
     *  Buzz numbers. They are numbers that are either divisible by 7 or end with 7
     * @param number
     * @return
     */
    public static boolean isBuzz(long number) {
        int endWith = Math.toIntExact(number % 10);

        return (number % BUZZ_NUMBER == 0 || endWith == BUZZ_NUMBER);
    }

    /**
     * A Duck number is a positive number that contains zeroes. For example, 3210, 8050896, 70709 are
     * Duck numbers.
     * @param number
     * @return
     */
    public static boolean isDuck(long number) {
        return Long.toString(number).contains("0");
    }

    /**
     * A Palindromic number is symmetrical; in other words, it stays the same regardless of whether we
     * read it from left or right. For example, 17371 is a palindromic number. 5 is also a palindromic
     * number. 1234 is not. If read it from right, it becomes 4321. Add this new property to our program.
     * @param number
     * @return
     */
    public static boolean isPalindromic(long number) {
        String str = Long.toString(number);
        String reverseStr = new StringBuilder(str).reverse().toString();
        return str.equals(reverseStr);
    }
}