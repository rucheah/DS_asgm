import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Flight {
    // Unique identifier for the flight
    String flightID;
    // Maximum number of seats available on the flight
    public static final int DEFAULT_MAX_SEATS = 5;
    // List of passengers with confirmed tickets
    ArrayList<Passenger> confirmedTicketList;
    // Queue of passengers on the waiting list
    Queue<Passenger> waitingList;
    // Indicates if the flight has available seats
    boolean vacancyStatus;
    // Number of confirmed seats
    private int confirmedSeats;
    // Number of empty seats
    private int emptySeats;


    public Flight(String flightID) {
        this.flightID = flightID;
        this.confirmedTicketList = new ArrayList<>();
        this.waitingList = new LinkedList<>();
        vacancyStatus = true;
        confirmedSeats = 0;
        emptySeats = DEFAULT_MAX_SEATS;
    }
   // Checks if the flight is fully booked
    public boolean isFull() {
        return confirmedTicketList.size() >= DEFAULT_MAX_SEATS;
    }

   // Updates the vacancy status of the flight
    public void updateVacancyStatus() {
        vacancyStatus = !isFull();
    }

     // Books a flight for a passenger
     public void bookFlight(Passenger passenger, Flight flight) {

        Ticket bookingTicket = new Ticket(passenger, flight);

        if (!isFull()) {
            bookingTicket.setStatus(Ticket.TicketStatus.CONFIRMED);
             addConfirmedPassenger(passenger);
            System.out.print("Ticket confirmed for passenger: " + passenger.getName());
        } else {
            bookingTicket.setStatus(Ticket.TicketStatus.WAITLISTED);
            addWaitlistedPassenger(passenger);
            System.out.print("The flight is fully booked. Passenger " + passenger.getName() + " added to the waiting list.");
        }
         passenger.addTicketToBucket(bookingTicket);
        Main.afs.tickets.put(bookingTicket.getTicketId(), bookingTicket);
        updateVacancyStatus();
    }

    // Adds a passenger to the confirmed list
    public void addConfirmedPassenger(Passenger passenger) {
           if (!isFull()) {
                 confirmedTicketList.add(passenger);
                 confirmedSeats++;
                 emptySeats--;
               }
            else{
               System.err.println("The flight is full and can't add any more passenger");
           }
    }

    // Adds a passenger to the waiting list
    public void addWaitlistedPassenger(Passenger passenger) {
        waitingList.add(passenger);
    }

    // Processes the waiting list by moving passengers to the confirmed list if seats are available
    public void processWaitlist() {
       while (!isFull() && !waitingList.isEmpty()) {
           Passenger nextPassenger = waitingList.poll();
            addConfirmedPassenger(nextPassenger);
        }
    }

    // Cancels a ticket for a passenger
    public boolean cancelTicket(Passenger passenger){
       // Search all the tickets of this passenger on this flight
        Ticket ticketToBeRemoved = null;
        for(Ticket ticket : passenger.getTickets()){
           if (ticket.getFlight().equals(this)) {
               ticketToBeRemoved = ticket;
               break;
            }
        }
        // If ticket is not found return false
       if(ticketToBeRemoved == null){
            System.out.println("Ticket not found for passenger: "+ passenger.getName() + " and flight " + this.flightID);
            return false;
        }

        if(confirmedTicketList.remove(passenger)){
            confirmedSeats--;
            emptySeats++;
            System.out.println("Ticket canceled for passenger: " + passenger.getName());
              // Remove from centralized tickets
              Main.afs.tickets.remove(ticketToBeRemoved.getTicketId());
              // Remove from passenger tickets
              passenger.getTickets().remove(ticketToBeRemoved);

            if(!waitingList.isEmpty()){
                Passenger nextPassenger = waitingList.poll();
                 addConfirmedPassenger(nextPassenger);
                System.out.println("Passenger " + nextPassenger.getName() + " moved from waiting list to confirmed.");
            }
            updateVacancyStatus();
             updateTicketStatuses();
            return true;
        }else{
            System.out.println("Passenger not found in the confirmed list.");
            return false;
        }
    }

   // Updates ticket statuses based on passenger's list
    public void updateTicketStatuses() {
        for (Passenger p : confirmedTicketList) {
            for (Ticket t : p.getTickets()) {
                if (t.getFlight().equals(this)) {
                    t.setStatus(Ticket.TicketStatus.CONFIRMED);
                }
            }
        }

        for (Passenger p : waitingList) {
            for (Ticket t : p.getTickets()) {
                if (t.getFlight().equals(this)) {
                    t.setStatus(Ticket.TicketStatus.WAITLISTED);
                }
            }
        }
    }

   // Sets the number of confirmed seats with validation
    public void setConfirmedSeats(int confirmedSeats) {
        if (confirmedSeats < 0 || confirmedSeats > DEFAULT_MAX_SEATS) {
            System.err.println("Invalid confirmed seats value. Adjusting to default.");
            confirmedSeats = Math.max(0, Math.min(confirmedSeats, DEFAULT_MAX_SEATS));
        }
        this.confirmedSeats = confirmedSeats;
        this.emptySeats = DEFAULT_MAX_SEATS - confirmedSeats;
    }

    // Sets the number of empty seats with validation
    public void setEmptySeats(int emptySeats) {
        if (emptySeats < 0 || emptySeats > DEFAULT_MAX_SEATS) {
            throw new IllegalArgumentException("Invalid empty seats value.");
        }
        this.emptySeats = emptySeats;
        this.confirmedSeats = DEFAULT_MAX_SEATS - emptySeats;
    }

   // Returns the number of confirmed seats
    public int getConfirmedSeats() {
        return confirmedSeats;
    }

    // Returns the number of empty seats
    public int getEmptySeats() {
        return emptySeats;
    }

   // Returns the number of passengers on the waiting list
    public int getWaitlistCount() {
        return waitingList.size();
    }


    @Override
    // Returns a string representation of the flight
    public String toString() {
        return "\nFlight ID: " + this.flightID +
                "\nTotal seat available: " + DEFAULT_MAX_SEATS +
                "\nSeat booked: " + confirmedSeats +
                "\nSeat available: " + emptySeats +
                "\nVacancy Status: " + vacancyStatus +
                "\nWaiting list count: " + waitingList.size();
    }
}
