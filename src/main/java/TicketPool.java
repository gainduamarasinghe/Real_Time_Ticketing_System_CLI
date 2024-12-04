import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketPool {
    private final int maximumTicketCapacity;
    private final int totalTicketsToSell;
    private Queue<Ticket> ticketsQueue;
    private int ticketCounter = 1;
    private int totalTicketsReleased = 0;
    private int totalTicketsSold = 0;

    // Separate ID maps and counters for vendors and customers
    private final ConcurrentHashMap<Thread, Integer> vendorIdMap = new ConcurrentHashMap<>();
    private final AtomicInteger vendorIdCounter = new AtomicInteger(1);

    private final ConcurrentHashMap<Thread, Integer> customerIdMap = new ConcurrentHashMap<>();
    private final AtomicInteger customerIdCounter = new AtomicInteger(1);

    public TicketPool(int maximumTicketCapacity, int totalTicketsToSell) {
        this.maximumTicketCapacity = maximumTicketCapacity;
        this.totalTicketsToSell = totalTicketsToSell;
        this.ticketsQueue = new LinkedList<>();
    }

    // Get or assign a unique ID for vendors
    private int getVendorId() {
        return vendorIdMap.computeIfAbsent(Thread.currentThread(), t -> vendorIdCounter.getAndIncrement());
    }

    // Get or assign a unique ID for customers
    private int getCustomerId() {
        return customerIdMap.computeIfAbsent(Thread.currentThread(), t -> customerIdCounter.getAndIncrement());
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
        int vendorId = getVendorId();
        System.out.println("Ticket added by Vendor-" + vendorId + " - current size is " + ticketsQueue.size());
        Logger.log("Ticket added by Vendor-" + vendorId + " - current size is " + ticketsQueue.size());
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
            int customerId = getCustomerId();
            System.out.println("Ticket bought by Customer-" + customerId + " - current size is " + ticketsQueue.size() + " - Ticket is " + ticket);
            Logger.log("Ticket bought by Customer-" + customerId + " - current size is " + ticketsQueue.size() + " - Ticket is " + ticket);
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

    public int getVendorIdForThread() {
        return getVendorId(); // Call the private method to get the vendor ID
    }
    public int getCustomerIdForThread() {
        return getCustomerId(); // Call the private method to get the customer ID
    }

}
