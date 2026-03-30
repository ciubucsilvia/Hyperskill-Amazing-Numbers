package dev;

import java.util.*;
import java.util.stream.Collectors;

/**
 *Your program should process the user requests. In this stage, your program should:
 *
 * 1. Welcome users;
 * 2. Display the instructions;
 * 3. Ask for a request;
 * 4. If a user enters an empty request, print the instructions;
 * 5. If the user enters zero, terminate the program;
 * 6. If numbers are not natural, print the error message;
 * 7. If an incorrect property is specified, print the error message and the list of available properties;
 * 8. For one number, print the properties of the number;
 * 9. For two numbers, print the properties of all numbers in the list;
 * 10. For two numbers and two properties, print the list of numbers that contain the specified properties;
 * 11. If a property is preceded by a minus, this property should not be present in a number;
 * 12. If the user specifies mutually exclusive properties, abort the request and warn the user.
 * 13. Once the request is processed, continue execution from step 3.
 *
 * In this stage, property names include even, odd, buzz, duck, palindromic, gapful, spy, sunny, square, jumping, sad, and happy. Mutually exclusive properties are even/odd, duck/spy, sunny/square, sad/happy pairs, as well as direct opposites (property and -property). The test won't check the order of properties, their indentation, and spaces. You may format numbers as you like.
 */
public class Main {

    // Shared scanner for reading user input from the console
    protected static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        welcome();
        displayInstructions();

        while (true) {
            String request = getRequest();

            // If a user enters an empty request, print instructions and prompt again
            if(request.isBlank()) {
                displayInstructions();
                continue;
            }

            // Split the input string into parameters based on whitespace
            String[] params = request.trim().split("\\s+");

            try {
                // The first parameter must be the starting number
                long number = Long.parseLong(params[0]);

                // Terminate the program if the user enters 0
                if(number == 0) {
                    System.out.println("Goodbye!");
                    break;
                }

                // Numbers must be positive (natural numbers)
                if(!isNatural(number)) {
                    System.out.println("The first parameter should be a natural number or zero.\n");
                    continue;
                }

                AmazingNumber amazingNumber = new AmazingNumber(number);

                // Only one number entered -> Show detailed properties of that specific number
                if(params.length == 1) {
                    System.out.println(amazingNumber.getProperties());
                }
                // Two numbers entered -> Show a list of 'step' numbers starting from 'number'
                else if(params.length == 2) {
                    int step = Integer.parseInt(params[1]);
                    if (step < 1) {
                        System.out.println("The second parameter should be a natural number.\n");
                        continue;
                    }
                    amazingNumber.setStep(step);
                    System.out.println(amazingNumber.getPropertiesWithStep());
                }
                // Two numbers and properties entered -> Search and filter based on properties
                else {
                    int step = Integer.parseInt(params[1]);
                    if (step < 1) {
                        System.out.println("The second parameter should be a natural number.\n");
                        continue;
                    }

                    // Collect properties from index 2 onwards and normalize to uppercase
                    Set<String> properties = new HashSet<>();
                    for(int i = 2; i < params.length; i++) {
                        properties.add(params[i].toUpperCase());
                    }

                    // Separate valid properties from invalid ones for error reporting
                    Set<String> invalidProperties = new HashSet<>();
                    for(String prop: properties) {
                        if(!isValidProperty(prop)) {
                            invalidProperties.add(prop);
                        }
                    }

                    // Report errors if any property name is unrecognized
                    if(!invalidProperties.isEmpty()) {
                        String propertyList = invalidProperties.stream()
                                .sorted()
                                .collect(Collectors.joining(", "));
                        String message = invalidProperties.size() == 1
                                ? String.format("The property [%s] is wrong.", propertyList)
                                : String.format("The properties [%s] are wrong.", propertyList);
                        System.out.println(message);
                        System.out.println("Available properties: " + getAvailableProperties() + "\n");
                        continue;
                    }

                    // Check for impossible combinations (e.g., EVEN and ODD or SUNNY and -SUNNY)
                    Set<String> exclusiveError = checkMutuallyExclusive(properties);
                    if(!exclusiveError.isEmpty()) {

                        System.out.printf("The request contains mutually exclusive properties: %s\n", exclusiveError);
                        System.out.println("There are no numbers with these properties.\n");
                        continue;
                    }

                    // Perform search: Find 'step' amount of numbers matching all criteria
                    amazingNumber.setStep(step);
                    amazingNumber.setFilteredProperties(properties);
                    System.out.println(amazingNumber.getFilteredProperties());
                }
            } catch (NumberFormatException e) {
                // Handle cases where non-numeric strings are entered instead of numbers
                System.out.println("The first parameter should be a natural number or zero.\n");
            }
        }
        scanner.close();
    }


    protected static void welcome() {
        System.out.println("Welcome to Amazing Numbers!\n");
    }

    protected static void displayInstructions() {
        System.out.println("Supported requests:");
        System.out.println("- enter a natural number to know its properties;");
        System.out.println("- enter two natural numbers to obtain the properties of the list:");
        System.out.println("  * the first parameter represents a starting number;");
        System.out.println("  * the second parameter shows how many consecutive numbers are to be printed;");
        System.out.println("- two natural numbers and properties to search for;");
        System.out.println("- a property preceded by minus must not be present in numbers;");
        System.out.println("- separate the parameters with one space;");
        System.out.println("- enter 0 to exit.\n");
    }

    protected static String getRequest() {
        System.out.print("Enter a request: ");
        return scanner.nextLine();
    }

    public static boolean isNatural(long number) { return number >= 1; }

    /**
     * Checks if a property exists in the NumberProperty enum.
     * Supports negative properties (starting with '-') by stripping the prefix before check.
     */
    public static boolean isValidProperty(String property) {
        String cleanProp = property.startsWith("-") ? property.substring(1) : property;
        try {
            NumberProperty.valueOf(cleanProp);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String getAvailableProperties() {
        return Arrays.toString(NumberProperty.values());
    }

    /**
     * Detects mutually exclusive logic in the user's property set.
     * @return A set containing the conflicting properties, or an empty set if clear.
     */
    public static Set<String> checkMutuallyExclusive(Set<String> properties) {
        List<Set<String>> pairs = Arrays.asList(
                new HashSet<>(Arrays.asList("EVEN", "ODD")),
                new HashSet<>(Arrays.asList("-EVEN", "-ODD")),
                new HashSet<>(Arrays.asList("DUCK", "SPY")),
                new HashSet<>(Arrays.asList("SUNNY", "SQUARE")),
                new HashSet<>(Arrays.asList("HAPPY", "SAD")),
                new HashSet<>(Arrays.asList("-HAPPY", "-SAD"))
        );

        for(Set<String> pair: pairs) {
            if(properties.containsAll(pair)) return pair;
        }

        // Check for direct opposites (e.g., DUCK and -DUCK)
        for (NumberProperty p : NumberProperty.values()) {
            if (properties.contains(p.name()) && properties.contains("-" + p.name())) {
                return new HashSet<>(Arrays.asList(p.name(), "-" + p.name()));
            }
        }
        return Collections.emptySet();
    }
}

/**
 * Class representing a number and the logic to calculate its "amazing" properties.
 */
class AmazingNumber {
    private long number;
    private int step;
    private Set<String> filteredProperties;

    public AmazingNumber(long number) { this.number = number; }
    public void setStep(int step) { this.step = step; }
    public void setFilteredProperties(Set<String> props) { this.filteredProperties = props; }

    /**
     * Returns a vertical list of all properties for a single number.
     */
    public String getProperties() {
        return String.format("Properties of %,d\n", number) +
                String.format("        buzz: %b\n", isBuzz()) +
                String.format("        duck: %b\n", isDuck()) +
                String.format(" palindromic: %b\n", isPalindromic()) +
                String.format("      gapful: %b\n", isGapful()) +
                String.format("         spy: %b\n", isSpy()) +
                String.format("      square: %b\n", isSquare()) +
                String.format("       sunny: %b\n", isSunny()) +
                String.format("     jumping: %b\n", isJumping()) +
                String.format("       happy: %b\n", isHappy()) +
                String.format("         sad: %b\n", !isHappy()) +
                String.format("        even: %b\n", isEven()) +
                String.format("         odd: %b\n", !isEven());
    }

    /**
     * Returns properties for a range of consecutive numbers.
     */
    public String getPropertiesWithStep() {
        StringBuilder sb = new StringBuilder();
        for(long i = 0; i < step; i++) {
            sb.append(printFormattedLine(number + i)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Searches for numbers that match a specific set of criteria.
     * Continues checking numbers until 'step' count of matches is reached.
     */
    protected String getFilteredProperties() {
        StringBuilder sb = new StringBuilder();
        long current = number;
        int found = 0;
        while (found < step) {
            AmazingNumber an = new AmazingNumber(current);
            if (hasAllMatches(an, filteredProperties)) {
                sb.append(printFormattedLine(current)).append("\n");
                found++;
            }
            current++;
        }
        return sb.toString();
    }

    /**
     * Checks if a number satisfies all positive and negative property constraints.
     */
    private boolean hasAllMatches(AmazingNumber an, Set<String> props) {
        for (String p : props) {
            boolean isNeg = p.startsWith("-");
            String clean = isNeg ? p.substring(1) : p;
            boolean has = an.hasProperty(clean);

            // Logic: if it's negated, the number must NOT have the property.
            // If it's positive, the number MUST have the property.
            if (isNeg && has) return false;
            if (!isNeg && !has) return false;
        }
        return true;
    }

    /**
     * Maps property names to their respective boolean logic methods.
     */
    private boolean hasProperty(String property) {
        switch (NumberProperty.valueOf(property)) {
            case BUZZ: return isBuzz();
            case DUCK: return isDuck();
            case PALINDROMIC: return isPalindromic();
            case GAPFUL: return isGapful();
            case SPY: return isSpy();
            case SQUARE: return isSquare();
            case SUNNY: return isSunny();
            case JUMPING: return isJumping();
            case HAPPY: return isHappy();
            case SAD: return !isHappy();
            case EVEN: return isEven();
            case ODD: return !isEven();
            default: return false;
        }
    }

    /**
     * Formats a single line output (e.g., "123 is buzz, duck, even").
     */
    protected String printFormattedLine(long num) {
        AmazingNumber an = new AmazingNumber(num);
        StringJoiner sj = new StringJoiner(", ");
        if (an.isBuzz()) sj.add("buzz");
        if (an.isDuck()) sj.add("duck");
        if (an.isPalindromic()) sj.add("palindromic");
        if (an.isGapful()) sj.add("gapful");
        if (an.isSpy()) sj.add("spy");
        if (an.isSquare()) sj.add("square");
        if (an.isSunny()) sj.add("sunny");
        if (an.isJumping()) sj.add("jumping");
        if (an.isHappy()) sj.add("happy");
        if (!an.isHappy()) sj.add("sad");
        if (an.isEven()) sj.add("even");
        if (!an.isEven()) sj.add("odd");

        return String.format("%,14d is %s", num, sj.toString());
    }

    // --- PROPERTY LOGIC METHODS ---

    public boolean isEven() { return number % 2 == 0; }

    // Divisible by 7 OR ends with 7
    public boolean isBuzz() { return number % 7 == 0 || number % 10 == 7; }

    // Contains at least one zero
    public boolean isDuck() { return String.valueOf(number).contains("0"); }

    // Reads the same forwards and backwards
    public boolean isPalindromic() {
        String s = String.valueOf(number);
        return s.equals(new StringBuilder(s).reverse().toString());
    }

    // 3+ digits, divisible by concatenation of first and last digit
    public boolean isGapful() {
        String s = String.valueOf(number);
        if (s.length() < 3) return false;
        int div = Integer.parseInt(s.substring(0, 1) + s.substring(s.length() - 1));
        return number % div == 0;
    }

    // Sum of digits equals product of digits
    public boolean isSpy() {
        long sum = 0, prod = 1, n = number;
        while (n > 0) {
            long d = n % 10;
            sum += d; prod *= d; n /= 10;
        }
        return sum == prod;
    }

    // N is sunny if N + 1 is a perfect square
    public boolean isSunny() { return isSquare(number + 1); }

    // Number is a perfect square
    public boolean isSquare() { return isSquare(number); }

    private boolean isSquare(long n) {
        long sqrt = (long) Math.sqrt(n);
        return sqrt * sqrt == n;
    }

    // Adjacent digits differ by exactly 1
    public boolean isJumping() {
        String s = String.valueOf(number);
        for (int i = 0; i < s.length() - 1; i++) {
            if (Math.abs(s.charAt(i) - s.charAt(i + 1)) != 1) return false;
        }
        return true;
    }

    // Sequence of sum of squares of digits eventually reaches 1
    public boolean isHappy() {
        Set<Long> seen = new HashSet<>();
        long n = number;
        while (n != 1 && !seen.contains(n)) {
            seen.add(n);
            long sum = 0;
            while (n > 0) {
                long d = n % 10; sum += d * d; n /= 10;
            }
            n = sum;
        }
        return n == 1;
    }
}

/**
 * Enumeration of all supported amazing number properties.
 */
enum NumberProperty {
    EVEN, ODD, BUZZ, DUCK, PALINDROMIC, GAPFUL, SPY, SQUARE, SUNNY, JUMPING, HAPPY, SAD;
}