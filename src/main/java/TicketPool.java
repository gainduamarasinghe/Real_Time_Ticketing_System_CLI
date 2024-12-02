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
            if (totalTicketsReleased >= totalTicketsToSell) return;
            try {
                System.out.println("waiting to add......");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        Ticket ticket = new Ticket(ticketCounter++, "Simple Event", new BigDecimal("1000"), "Maharagama");
        ticketsQueue.add(ticket);
        totalTicketsReleased++;
        notifyAll();
        System.out.println("Ticket added by - " + Thread.currentThread().getName() + " - current size is - " + ticketsQueue.size());
        Logger.log("Ticket added by - " + Thread.currentThread().getName() + " - current size is - " + ticketsQueue.size());
    }

    public synchronized Ticket buyTicket() {
        while (ticketsQueue.isEmpty() && totalTicketsSold < totalTicketsToSell) {
            try {
                System.out.println("customer waiting to buy.....");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        if (!ticketsQueue.isEmpty()) {
            Ticket ticket = ticketsQueue.poll();
            totalTicketsSold++;
            notifyAll();
            System.out.println("Ticket bought by - " + Thread.currentThread().getName() + " - current size is - " + ticketsQueue.size() + " - Ticket is - " + ticket);
            Logger.log("Ticket bought by - " + Thread.currentThread().getName() + " - current size is - " + ticketsQueue.size() + " - Ticket is - " + ticket);
            return ticket;
        }
        return null;
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
