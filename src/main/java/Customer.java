public class Customer implements Runnable {
    private TicketPool ticketPool;
    private int customerRetrievalRate;
    private int quantity;

    public Customer(TicketPool ticketPool, int customerRetrievalRate, int quantity) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
        this.quantity = quantity;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !ticketPool.shouldStop()) {
            ticketPool.buyTicket();
            try {
                Thread.sleep(customerRetrievalRate * 100);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " interrupted while retrieving tickets.");
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println(Thread.currentThread().getName() + " stopped.");
    }
}
