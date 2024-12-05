package lk.system.ticketing.ticketpool;

import java.math.BigDecimal;

public class Ticket {
    private int ticketID;
    private String eventName;
    private BigDecimal ticketPrice;
    private String location;

    public Ticket(int ticketID, String eventName, BigDecimal ticketPrice, String location) {
        this.ticketID = ticketID;
        this.eventName = eventName;
        this.ticketPrice = ticketPrice;
        this.location = location;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Ticket is {" +
                "ticketID=" + ticketID +
                ", eventName='" + eventName + '\'' +
                ", ticketPrice=" + ticketPrice +
                ", location='" + location + '\'' +
                '}';
    }
}
