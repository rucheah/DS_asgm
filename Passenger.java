import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Passenger {
    // Unique identifier for the passenger
    private String passportNum;
    // Name of the passenger
    private String name;
    // List to store all the tickets for this passenger
    private ArrayList<Ticket> ticketsBucket;

    // Constructor to create a new passenger
    public Passenger(String passportNum, String name){
        this.passportNum = passportNum;
        this.name = name;
        ticketsBucket = new ArrayList<>();
    }

    // Returns the name of the passenger
    public String getName() {
        return name;
    }

    // Returns the passport number of the passenger
    public String getPassportNum() {
        return passportNum;
    }

    // Sets the name of the passenger
    public void setName(String name) {
        this.name = name;
    }

    // Sets the passport number of the passenger
    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

    // Adds a ticket to the passenger's ticket bucket
    public void addTicketToBucket(Ticket ticket) {
        ticketsBucket.add(ticket);
    }

    // Returns the list of tickets for the passenger
    public List<Ticket> getTickets() {
        return ticketsBucket;
    }

     // Displays the ticket status for the passenger
    public void viewTicketStatus(){

        if (ticketsBucket.isEmpty()) {
            System.out.print("You haven't booked any flights yet.");
        } else {
           for (Ticket ticket : ticketsBucket) {
             String flightID = ticket.getFlight().flightID;
                Ticket.TicketStatus ticketStatus = ticket.getStatus();
                String statusString = ticketStatus != null ? ticketStatus.toString() : "N/A";
                System.out.println("Flight ID: " + flightID
                        + "\nTicket Status: " + statusString);
            }
        }
    }

     // Allows a passenger to edit their details
    public void editPassengerDetails(String newName, String newPassportNumber){
        if(newName != null && !newName.isEmpty()){
            this.name = newName;
        }
        if(newPassportNumber != null && !newPassportNumber.isEmpty()){
            this.passportNum = newPassportNumber;
        }
        System.out.println("Passenger details updated successfully.");
    }

    // Returns a string representation of the passenger
    @Override
    public String toString(){
        return "Passenger Information: "+
                "\nName: "+this.name+
                "\nPassport Number: "+this.passportNum;
    }

   // Checks if two passengers are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(passportNum, passenger.passportNum);
    }

    // Returns the hash code for the passenger
    @Override
    public int hashCode() {
        return Objects.hash(passportNum);
    }
}
