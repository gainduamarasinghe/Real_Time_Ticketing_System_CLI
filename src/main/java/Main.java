import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<Thread> vendorThreads = new ArrayList<>();
    private static List<Thread> customerThreads = new ArrayList<>();
    private static TicketPool ticketPool;
    private static boolean running = false;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // Get configuration inputs from user
        int totalAvailableTickets = InputValidation.getValidTickets(scan, "Enter the total number of tickets: ");
        int ticketReleaseRate = InputValidation.getValidReleaseRate(scan, "Enter the ticket release rate (in seconds): ");
        int customerRetrievalRate = InputValidation.getValidRetrievalRate(scan, "Enter the customer retrieval rate (in seconds): ");
        int maximumTicketCapacity = InputValidation.getValidMaxTickets(scan, "Enter the maximum number of tickets: ");

        System.out.println("\nAll inputs validated successfully!");
        System.out.println("Total Tickets: " + totalAvailableTickets);
        System.out.println("Ticket Release Rate: " + ticketReleaseRate);
        System.out.println("Customer Retrieval Rate: " + customerRetrievalRate);
        System.out.println("Maximum Ticket Capacity: " + maximumTicketCapacity);

        // Save configuration
        Configuration configuration = new Configuration(totalAvailableTickets, ticketReleaseRate, customerRetrievalRate, maximumTicketCapacity);
        ConfigurationManager.saveConfigToJson(configuration);

        // Initialize TicketPool
        ticketPool = new TicketPool(maximumTicketCapacity, totalAvailableTickets);

        while (true) {
            System.out.print("\nEnter command (start/stop/exit): ");
            String command = scan.nextLine().trim().toLowerCase();

            // Skip blank input
            if (command.isEmpty()) {
                continue; // Do not process blank inputs
            }

            switch (command) {
                case "start":
                    if (!running) {
                        startTicketingSystem(totalAvailableTickets, ticketReleaseRate, customerRetrievalRate);
                        running = true;
                        System.out.println("Ticket selling system started.");
                    } else {
                        System.out.println("System is already running.");
                    }
                    break;

                case "stop":
                    if (running) {
                        stopTicketingSystem();
                        running = false;
                        System.out.println("Ticket selling system stopped.");
                    } else {
                        System.out.println("System is not running yet.");
                    }
                    break;

                case "exit":
                    if (running) {
                        stopTicketingSystem();
                    }
                    System.out.println("Exiting the program...");
                    scan.close();
                    System.exit(0); // Forcefully exit the program
                    break;

                default:
                    System.out.println("Invalid command! Please enter 'start', 'stop', or 'exit'.");
                    break;
            }
        }
    }

    private static void startTicketingSystem(int totalAvailableTickets, int ticketReleaseRate, int customerRetrievalRate) {
        int numberOfVendors = 10;

        for (int i = 0; i < numberOfVendors; i++) {
            Vendor vendor = new Vendor(totalAvailableTickets / numberOfVendors, ticketReleaseRate, ticketPool);
            Thread vendorThread = new Thread(vendor, "Vendor ID-" + i);
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        int numberOfCustomers = 10;

        for (int i = 0; i < numberOfCustomers; i++) {
            Customer customer = new Customer(ticketPool, customerRetrievalRate, totalAvailableTickets / numberOfCustomers);
            Thread customerThread = new Thread(customer, "Customer ID-" + i);
            customerThreads.add(customerThread);
            customerThread.start();
        }
    }

    private static void stopTicketingSystem() {
        // Interrupt all vendor threads
        for (Thread vendorThread : vendorThreads) {
            vendorThread.interrupt();
            try {
                vendorThread.join(); // Wait for the thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Error while stopping vendor thread: " + vendorThread.getName());
            }
        }
        vendorThreads.clear();

        // Interrupt all customer threads
        for (Thread customerThread : customerThreads) {
            customerThread.interrupt();
            try {
                customerThread.join(); // Wait for the thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Error while stopping customer thread: " + customerThread.getName());
            }
        }
        customerThreads.clear();

        System.out.println("All threads stopped.");
    }
}
