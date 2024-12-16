import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Passenger {

    private String passportNum;
    private String name;
    private ArrayList<Ticket> ticketsBucket;

    public Passenger(String passportNum, String name){
        this.passportNum = passportNum;
        this.name = name;
        ticketsBucket = new ArrayList<>();
    }


    public String getName() {
        return name;
    }


    public String getPassportNum() {
        return passportNum;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }


  public void addTicketToBucket(Ticket ticket) {
        ticketsBucket.add(ticket);
    }


    public List<Ticket> getTickets() {
        return ticketsBucket;
    }

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


    public void editPassengerDetails(String newName, String newPassportNumber){
        if(newName != null && !newName.isEmpty()){
            this.name = newName;
        }
        if(newPassportNumber != null && !newPassportNumber.isEmpty()){
            this.passportNum = newPassportNumber;
        }
        System.out.println("Passenger details updated successfully.");
    }

    @Override
    public String toString(){
        return "Passenger Information: "+
                "\nName: "+this.name+
                "\nPassport Number: "+this.passportNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(passportNum, passenger.passportNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passportNum);
    }
}