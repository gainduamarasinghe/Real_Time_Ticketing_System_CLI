import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    private static ThreadPoolExecutor vendorExecutor;
    private static ThreadPoolExecutor customerExecutor;
    private static TicketPool ticketPool;
    private static volatile boolean running = false;
    private static volatile boolean stopRequested = false;
    private static Thread commandListener;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Welcome configuration ");
        // Getting inputs
        int totalAvailableTickets = InputValidation.getValidTickets(scan, "Enter the total number of tickets: ");
        int ticketReleaseRate = InputValidation.getValidReleaseRate(scan, "Enter the ticket release rate (in seconds): ");
        int customerRetrievalRate = InputValidation.getValidRetrievalRate(scan, "Enter the customer retrieval rate (in seconds): ");
        int maximumTicketCapacity = InputValidation.getValidMaxTickets(scan, "Enter the maximum number of tickets: ");

        System.out.println("\nAll inputs validated successfully!");
        System.out.println();
        System.out.println("Total Tickets: " + totalAvailableTickets);
        System.out.println("Ticket Release Rate: " + ticketReleaseRate);
        System.out.println("Customer Retrieval Rate: " + customerRetrievalRate);
        System.out.println("Maximum Ticket Capacity: " + maximumTicketCapacity);

        Configuration configuration = new Configuration(totalAvailableTickets, ticketReleaseRate, customerRetrievalRate, maximumTicketCapacity);
        ConfigurationManager.saveConfigToJson(configuration);

        ticketPool = new TicketPool(maximumTicketCapacity, totalAvailableTickets);

        System.out.println();
        System.out.println("Enter command to start or stop the system (start/q):");
        // Start a separate thread to listen for "start" and "q" commands
        commandListener = new Thread(() -> listenForCommands(totalAvailableTickets, ticketReleaseRate, customerRetrievalRate));
        commandListener.start();
    }

    private static void listenForCommands(int totalAvailableTickets, int ticketReleaseRate, int customerRetrievalRate) {
        Scanner commandScanner = new Scanner(System.in);

        while (!stopRequested) {
            String command = commandScanner.nextLine().trim().toLowerCase();

            if (command.isEmpty()) {
                continue;
            }

            switch (command) {
                case "start":
                    if (!running) {
                        running = true;
                        System.out.println("Starting the ticketing system...");
                        startTicketingSystem(totalAvailableTickets, ticketReleaseRate, customerRetrievalRate);
                        monitorThreadPools(); // Start monitoring the thread pools
                    } else {
                        System.out.println("System is already running.");
                    }
                    break;

                case "q":
                    System.out.println("Stopping the system and exiting...");
                    stopRequested = true; // Signal all tasks to stop
                    stopThreads();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid command! Please enter 'start' or 'q'.");
                    break;
            }
        }

        System.out.println("Command listener thread is exiting.");
    }

    private static void startTicketingSystem(int totalAvailableTickets, int ticketReleaseRate, int customerRetrievalRate) {
        int numberOfVendors = 10;
        vendorExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfVendors);

        for (int i = 0; i < numberOfVendors; i++) {
            Vendor vendor = new Vendor(totalAvailableTickets / numberOfVendors, ticketReleaseRate, ticketPool);
            vendorExecutor.submit(vendor); // Submit tasks to the thread pool
        }

        int numberOfCustomers = 5;
        customerExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfCustomers);

        for (int i = 0; i < numberOfCustomers; i++) {
            Customer customer = new Customer(ticketPool, customerRetrievalRate, totalAvailableTickets / numberOfCustomers);
            customerExecutor.submit(customer); // Submit tasks to the thread pool
        }

        System.out.println("Ticketing system is now running.");
    }

    private static void monitorThreadPools() {
        new Thread(() -> {
            while (true) {
                if(vendorExecutor.getActiveCount()==0 && customerExecutor.getActiveCount()==0){

                    System.exit(0);
                }

                if (vendorExecutor.isTerminated() && customerExecutor.isTerminated()) {
                    System.out.println("All threads in the thread pools have completed.");
                    stopRequested = true; // Signal the command listener thread to stop
                    System.exit(0); // Exit the program
                }

                try {
                    Thread.sleep(1000); // Check periodically
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Monitor thread interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    private static void stopThreads() {
        try {
            if (vendorExecutor != null) {
                vendorExecutor.shutdownNow(); // Attempt to stop vendor threads
            }
            if (customerExecutor != null) {
                customerExecutor.shutdownNow(); // Attempt to stop customer threads
            }

            if (vendorExecutor != null) {
                vendorExecutor.awaitTermination(5, TimeUnit.SECONDS);
            }
            if (customerExecutor != null) {
                customerExecutor.awaitTermination(5, TimeUnit.SECONDS);
            }

            System.out.println("All threads have been stopped.");
        } catch (InterruptedException e) {
            System.err.println("Error stopping threads: " + e.getMessage());
        } finally {
            running = false;
            stopRequested = true; // Signal the command listener to exit
        }
    }
}
