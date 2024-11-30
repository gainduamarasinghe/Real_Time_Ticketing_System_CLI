import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;

public class TicketPool {
    private int maximumTicketCapacity;
    private Queue<Ticket> ticketsQueue;
    private int ticketCounter = 1; // Shared counter to ensure unique ticket IDs

    public TicketPool(int maximumTicketCapacity) {
        this.maximumTicketCapacity = maximumTicketCapacity;
        this.ticketsQueue = new LinkedList<>();
    }

    // Vendor adds tickets to the pool
    public synchronized void addTicket() {
        while (ticketsQueue.size() >= maximumTicketCapacity) {
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

        // Notify all waiting threads (customers) that a new ticket is available
        notifyAll();
        System.out.println("Ticket added by - " + Thread.currentThread().getName() + " - current size is - " + ticketsQueue.size());
    }

    // Customer buys a ticket from the pool
    public synchronized Ticket buyTicket() {
        while (ticketsQueue.isEmpty()) {
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
        Ticket ticket = ticketsQueue.poll();

        // Notify all waiting threads (vendors) that a ticket has been purchased
        notifyAll();
        System.out.println("Ticket bought by - " + Thread.currentThread().getName() + " - current size is - " + ticketsQueue.size() + " - Ticket is - " + ticket);
        return ticket;
    }
}
