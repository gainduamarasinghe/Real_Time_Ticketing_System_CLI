import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;

public class TicketPool {
    private final int maximumTicketCapacity;
    private final int totalTicketsToSell; // Total tickets that need to be released
    private Queue<Ticket> ticketsQueue;
    private int ticketCounter = 1; // Shared counter to ensure unique ticket IDs
    private int totalTicketsReleased = 0; // Number of tickets released by vendors
    private int totalTicketsSold = 0; // Number of tickets purchased by customers

    public TicketPool(int maximumTicketCapacity, int totalTicketsToSell) {
        this.maximumTicketCapacity = maximumTicketCapacity;
        this.totalTicketsToSell = totalTicketsToSell;
        this.ticketsQueue = new LinkedList<>();
    }

    // Vendor adds tickets to the pool
    public synchronized void addTicket() {
        // Wait if the queue is full or if all tickets have been released
        while (ticketsQueue.size() >= maximumTicketCapacity || totalTicketsReleased >= totalTicketsToSell) {
            if (totalTicketsReleased >= totalTicketsToSell) {
                return; // No more tickets to release
            }
            try {
                System.out.println(Thread.currentThread().getName() + " waiting to add tickets. Capacity reached.");
                wait(); // Wait until there is space to add tickets
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Vendor interrupted while waiting to add tickets.");
                return;
            }
        }

        // Create a new ticket with a unique ID and add it to the pool
        Ticket ticket = new Ticket(ticketCounter++, "Simple Event", new BigDecimal("1000"), "Maharagama");
        ticketsQueue.add(ticket);
        totalTicketsReleased++;

        // Notify all waiting threads (customers) that a new ticket is available
        notifyAll();
        System.out.println("Ticket added by - " + Thread.currentThread().getName() + " - current size is - " + ticketsQueue.size());
    }

    // Customer buys a ticket from the pool
    public synchronized Ticket buyTicket() {
        // Wait if the queue is empty and all tickets have not yet been released
        while (ticketsQueue.isEmpty() && totalTicketsSold < totalTicketsToSell) {
            try {
                System.out.println(Thread.currentThread().getName() + " waiting to buy tickets. No tickets available.");
                wait(); // Wait until a ticket is available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Customer interrupted while waiting to buy tickets.");
                return null; // Return null if the thread is interrupted
            }
        }

        // Retrieve and remove the ticket from the pool
        if (!ticketsQueue.isEmpty()) {
            Ticket ticket = ticketsQueue.poll();
            totalTicketsSold++;
            // Notify all waiting threads (vendors) that a ticket has been purchased
            notifyAll();
            System.out.println("Ticket bought by - " + Thread.currentThread().getName() + " - current size is - " + ticketsQueue.size() + " - Ticket is - " + ticket);
            return ticket;
        }

        return null; // If no tickets are available (all have been sold)
    }

    // Check if all tickets have been released
    public synchronized boolean allTicketsReleased() {
        return totalTicketsReleased >= totalTicketsToSell;
    }

    // Check if all tickets have been sold
    public synchronized boolean allTicketsSold() {
        return totalTicketsSold >= totalTicketsToSell;
    }
}
