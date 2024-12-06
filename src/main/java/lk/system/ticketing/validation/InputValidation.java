package lk.system.ticketing.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputValidation {
    private static List<Integer> userInputs = new ArrayList<>();

    // Validate total tickets
    public static int getValidTickets(Scanner scanner, String prompt) {
        int input;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input > 0) {
                    userInputs.add(input); // Store total tickets in userInputs
                    break;
                } else {
                    System.out.println("Total tickets must be a positive integer!");
                }
            } else {
                System.out.println("Invalid input! Please enter a positive integer.");
                scanner.next(); // Clear invalid input
            }
        }
        return input;
    }

    // Validate ticket release rate
    public static int getValidReleaseRate(Scanner scanner, String prompt) {
        int input;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input > 0) {
                    if (!userInputs.isEmpty() && input > userInputs.get(0)) {
                        System.out.println("The ticket release rate cannot exceed the total available tickets!");
                    } else {
                        userInputs.add(input); // Store release rate in userInputs
                        break;
                    }
                } else {
                    System.out.println("lk.system.ticketing.ticketpool.Ticket release rate must be a positive integer!");
                }
            } else {
                System.out.println("Invalid input! Please enter a positive integer.");
                scanner.next(); // Clear invalid input
            }
        }
        return input;
    }

    // Validate customer retrieval rate
    public static int getValidRetrievalRate(Scanner scanner, String prompt) {
        int input;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input > 0) {
                    if (userInputs.size() >= 2 && input > userInputs.get(1)) {
                        System.out.println("The customer retrieval rate cannot exceed the ticket release rate!");
                    } else {
                        userInputs.add(input); // Store retrieval rate in userInputs
                        break;
                    }
                } else {
                    System.out.println("lk.system.ticketing.users.Customer retrieval rate must be a positive integer!");
                }
            } else {
                System.out.println("Invalid input! Please enter a positive integer.");
                scanner.next(); // Clear invalid input
            }
        }
        return input;
    }

    // Validate maximum ticket capacity
    public static int getValidMaxTickets(Scanner scanner, String prompt) {
        int input;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input > 0) {
                    // Ensure max tickets do not exceed total tickets and are >= ticket release rate
                    if (userInputs.size() >= 2 && (input > userInputs.get(0) || input < userInputs.get(1))) {
                        System.out.println("The maximum ticket capacity cannot exceed the total tickets and must be greater than or equal to the ticket release rate!");
                    } else {
                        userInputs.add(input); // Store max capacity in userInputs
                        break;
                    }
                } else {
                    System.out.println("Maximum ticket capacity must be a positive integer!");
                }
            } else {
                System.out.println("Invalid input! Please enter a positive integer.");
                scanner.next(); // Clear invalid input
            }
        }
        return input;
    }
}
