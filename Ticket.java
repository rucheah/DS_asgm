import java.util.Objects;
import java.util.UUID;
public class Ticket {

    private String ticketId; // Unique ticket id
    Flight flight;
    Passenger passenger;
    private TicketStatus status;

    public enum TicketStatus {
        CONFIRMED,
        WAITLISTED,
        CANCELED
    }


    public Ticket(Passenger passenger, Flight flight){
       this.ticketId = UUID.randomUUID().toString();
        this.passenger = passenger;
        this.flight = flight;
        this.status = TicketStatus.WAITLISTED; // Set default ticket status
    }


    public TicketStatus getStatus() {
        return status;
    }

     public String getTicketId() {
        return ticketId;
    }


    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

     public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(ticketId, ticket.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }
}