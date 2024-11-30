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
    }
}
