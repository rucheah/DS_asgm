import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Flight {
    String flightID;
    public static final int DEFAULT_MAX_SEATS = 5;
    ArrayList<Passenger> confirmedTicketList;
    Queue<Passenger> waitingList;
    boolean vacancyStatus;
    private int confirmedSeats;
    private int emptySeats;


    public Flight(String flightID) {
        this.flightID = flightID;
        this.confirmedTicketList = new ArrayList<>();
        this.waitingList = new LinkedList<>();
        vacancyStatus = true;
        confirmedSeats = 0;
        emptySeats = DEFAULT_MAX_SEATS;
    }

    public boolean isFull() {
        return confirmedTicketList.size() >= DEFAULT_MAX_SEATS;
    }


    public void updateVacancyStatus() {
        vacancyStatus = !isFull();
    }

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


    public void addWaitlistedPassenger(Passenger passenger) {
        waitingList.add(passenger);
    }


    public void processWaitlist() {
       while (!isFull() && !waitingList.isEmpty()) {
           Passenger nextPassenger = waitingList.poll();
            addConfirmedPassenger(nextPassenger);
        }
    }

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


    public void setConfirmedSeats(int confirmedSeats) {
        if (confirmedSeats < 0 || confirmedSeats > DEFAULT_MAX_SEATS) {
            System.err.println("Invalid confirmed seats value. Adjusting to default.");
            confirmedSeats = Math.max(0, Math.min(confirmedSeats, DEFAULT_MAX_SEATS));
        }
        this.confirmedSeats = confirmedSeats;
        this.emptySeats = DEFAULT_MAX_SEATS - confirmedSeats;
    }


    public void setEmptySeats(int emptySeats) {
        if (emptySeats < 0 || emptySeats > DEFAULT_MAX_SEATS) {
            throw new IllegalArgumentException("Invalid empty seats value.");
        }
        this.emptySeats = emptySeats;
        this.confirmedSeats = DEFAULT_MAX_SEATS - emptySeats;
    }


    public int getConfirmedSeats() {
        return confirmedSeats;
    }


    public int getEmptySeats() {
        return emptySeats;
    }


    public int getWaitlistCount() {
        return waitingList.size();
    }


    @Override
    public String toString() {
        return "\nFlight ID: " + this.flightID +
                "\nTotal seat available: " + DEFAULT_MAX_SEATS +
                "\nSeat booked: " + confirmedSeats +
                "\nSeat available: " + emptySeats +
                "\nVacancy Status: " + vacancyStatus +
                "\nWaiting list count: " + waitingList.size();
    }
}