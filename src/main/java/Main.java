import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);

        int totalAvailableTickets = InputValidation.getValidTickets(scan,"Enter the total number of tickets: ");
        int ticketReleaseRate = InputValidation.getValidReleaseRate(scan, "Enter the ticket release rate: ");
        int customerRetrievalRate = InputValidation.getValidRetrievalRate(scan, "Enter the customer retrieval rate: ");
        int maximumTicketCapacity = InputValidation.getValidMaxTickets(scan, "Enter the maximum number of tickets: ");

        System.out.println("\nAll inputs validated successfully!");
        System.out.println("Total Tickets: " + totalAvailableTickets);
        System.out.println("Ticket Release Rate: " + ticketReleaseRate);
        System.out.println("Customer Retrieval Rate: " + customerRetrievalRate);
        System.out.println("Maximum Ticket Capacity: " + maximumTicketCapacity);

        Configuration configuration = new Configuration(totalAvailableTickets, ticketReleaseRate, customerRetrievalRate, maximumTicketCapacity);
        ConfigurationManager.saveConfigToJson(configuration);

        TicketPool ticketPool = new TicketPool(maximumTicketCapacity);

        Vendor[] vendors = new Vendor[10]; // Creating array of vendors
        for (int i = 0; i < vendors.length; i++) {
            vendors[i] = new Vendor(totalAvailableTickets, ticketReleaseRate, ticketPool);
            Thread vendorThread = new Thread(vendors[i], "Vendor ID-" + i);
            vendorThread.start();
        }

        Customer[] customers = new Customer[10]; // Creating array of customers
        for (int i = 0; i < customers.length; i++) {
            customers[i] = new Customer(ticketPool, customerRetrievalRate, 5); // Rerieve tickets from the pool
            Thread customerThread = new Thread(customers[i], "Customer ID-" + i);
            customerThread.start();
        }

    }
}
