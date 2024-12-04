import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;

public class TicketPool {
    private final int maximumTicketCapacity;
    private final int totalTicketsToSell;
    private Queue<Ticket> ticketsQueue;
    private int ticketCounter = 1;
    private int totalTicketsReleased = 0;
    private int totalTicketsSold = 0;

    public TicketPool(int maximumTicketCapacity, int totalTicketsToSell) {
        this.maximumTicketCapacity = maximumTicketCapacity;
        this.totalTicketsToSell = totalTicketsToSell;
        this.ticketsQueue = new LinkedList<>();
    }

    public synchronized void addTicket() {
        while (ticketsQueue.size() >= maximumTicketCapacity || totalTicketsReleased >= totalTicketsToSell) {
            if (totalTicketsReleased >= totalTicketsToSell) {
                return; // No more tickets to release
            }
            try {
                System.out.println("Waiting to add a ticket...");
                wait(); // Wait for space in the queue or more tickets to be sold
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt flag
                System.out.println("Ticket addition interrupted.");
                return; // Exit if interrupted
            }
        }
        Ticket ticket = new Ticket(ticketCounter++, "Simple Event", new BigDecimal("1000"), "Maharagama");
        ticketsQueue.add(ticket);
        totalTicketsReleased++;
        notifyAll(); // Notify waiting customers and vendors
        System.out.println("Ticket added by " + Thread.currentThread().getName() + " - current size is " + ticketsQueue.size());
        Logger.log("Ticket added by " + Thread.currentThread().getName() + " - current size is " + ticketsQueue.size());
    }

    public synchronized Ticket buyTicket() {
        while (ticketsQueue.isEmpty() && totalTicketsSold < totalTicketsToSell) {
            try {
                System.out.println("Customer waiting to buy...");
                wait(); // Wait for a ticket to be available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt flag
                System.out.println("Ticket purchase interrupted.");
                return null; // Exit if interrupted
            }
        }
        if (!ticketsQueue.isEmpty()) {
            Ticket ticket = ticketsQueue.poll();
            totalTicketsSold++;
            notifyAll(); // Notify vendor threads
            System.out.println("Ticket bought by " + Thread.currentThread().getName() + " - current size is " + ticketsQueue.size() + " - Ticket is " + ticket);
            Logger.log("Ticket bought by " + Thread.currentThread().getName() + " - current size is " + ticketsQueue.size() + " - Ticket is " + ticket);
            return ticket;
        }
        return null; // No ticket available, or stop condition met
    }

    public synchronized boolean shouldStop() {
        return allTicketsReleased() && allTicketsSold();
    }

    public synchronized boolean allTicketsReleased() {
        return totalTicketsReleased >= totalTicketsToSell;
    }

    public synchronized boolean allTicketsSold() {
        return totalTicketsSold >= totalTicketsToSell;
    }
}
