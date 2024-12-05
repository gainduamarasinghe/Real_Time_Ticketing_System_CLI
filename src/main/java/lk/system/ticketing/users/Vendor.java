package lk.system.ticketing.users;

import lk.system.ticketing.ticketpool.TicketPool;

public class Vendor implements Runnable {
    private int totalTickets;
    private int ticketReleaseRate;
    private TicketPool ticketPool;

    public Vendor(int totalTickets, int ticketReleaseRate, TicketPool ticketPool) {
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.ticketPool = ticketPool;
    }

    @Override
    public void run() {
        int vendorId = ticketPool.getVendorIdForThread(); // Get the vendor ID for the current thread
        while (!Thread.currentThread().isInterrupted() && !ticketPool.shouldStop()) {
            ticketPool.addTicket();
            try {
                Thread.sleep(ticketReleaseRate * 1000);
            } catch (InterruptedException e) {
                System.out.println("Vendor-" + vendorId + " interrupted while releasing tickets.");
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Vendor-" + vendorId + " stopped.");
    }

}
