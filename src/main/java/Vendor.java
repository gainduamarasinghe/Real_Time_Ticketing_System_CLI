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
        while (!Thread.currentThread().isInterrupted() && !ticketPool.shouldStop()) {
            ticketPool.addTicket();
            try {
                Thread.sleep(ticketReleaseRate * 100);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupted while releasing tickets.");
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println(Thread.currentThread().getName() + " stopped.");
    }
}
