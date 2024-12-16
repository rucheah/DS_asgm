import java.util.Objects;
import java.util.UUID;
public class Ticket {
    // Unique identifier for the ticket
    private String ticketId;
    // Flight associated with the ticket
    Flight flight;
    // Passenger associated with the ticket
    Passenger passenger;
    // Current status of the ticket
    private TicketStatus status;

    // Enum to represent the possible ticket statuses
    public enum TicketStatus {
        CONFIRMED,
        WAITLISTED,
        CANCELED
    }

    // Constructor to create a new ticket
    public Ticket(Passenger passenger, Flight flight){
       this.ticketId = UUID.randomUUID().toString();
        this.passenger = passenger;
        this.flight = flight;
        this.status = TicketStatus.WAITLISTED; // Set default ticket status
    }

    // Returns the current status of the ticket
    public TicketStatus getStatus() {
        return status;
    }

    // Returns the unique ID of the ticket
     public String getTicketId() {
        return ticketId;
    }

    // Returns the passenger associated with the ticket
    public Passenger getPassenger() {
        return passenger;
    }

    // Returns the flight associated with the ticket
    public Flight getFlight() {
        return flight;
    }

     // Sets the passenger for the ticket
     public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    // Sets the status of the ticket
    public void setStatus(TicketStatus status) {
        this.status = status;
    }

     // Sets the flight associated with the ticket
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

     // Checks if two tickets are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(ticketId, ticket.ticketId);
    }

    // Returns the hash code for the ticket
    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }
}
