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
        int customerId = ticketPool.getCustomerIdForThread(); // Get the customer ID for the current thread
        while (!Thread.currentThread().isInterrupted() && !ticketPool.shouldStop()) {
            ticketPool.buyTicket();
            try {
                Thread.sleep(customerRetrievalRate * 1000);
            } catch (InterruptedException e) {
                System.out.println("Customer-" + customerId + " interrupted while retrieving tickets.");
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Customer-" + customerId + " stopped.");
    }
}
