import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnnualFlightScheduler {
    // HashMap to store flights by date. Key: Date, Value: List of flights on that date
    static HashMap<LocalDate, ArrayList<Flight>> flightsByDate;
    // HashMap to store passenger details. Key: Passport Number, Value: Passenger Object
    static HashMap<String, Passenger> passengers;
    // HashMap to store ticket details. Key: Ticket ID, Value: Ticket object
    static HashMap<String, Ticket> tickets;  // Centralized ticket storage
    // Date formatter to parse date strings
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // CSV file name
    private static final String FILE_NAME = "C:\\Users\\ruche\\OneDrive\\Documents\\flights.csv";
    // Using an enum for status instead of strings would be ideal for the ticket

    public AnnualFlightScheduler() {
        flightsByDate = new HashMap<>();
        passengers = new HashMap<>();
        tickets = new HashMap<>();
        loadFlightsFromCsv();
    }
   // Loads flight data from the CSV file
   private void loadFlightsFromCsv() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 8) {
                    String flightID = parts[0];
                    LocalDate date;
                    String confirmedPassengersStr = parts[3];
                    String waitlistedPassengersStr = parts[4];

                    try {
                        date = LocalDate.parse(parts[1]);
                    } catch (Exception e) {
                        System.err.println("Invalid date format in CSV: " + parts[1]);
                        continue;
                    }

                    int confirmedSeats = 0;
                    int emptySeats = Flight.DEFAULT_MAX_SEATS;

                    try {
                        confirmedSeats = Integer.parseInt(parts[5]);
                        emptySeats = Integer.parseInt(parts[6]);

                        if (confirmedSeats + emptySeats != Flight.DEFAULT_MAX_SEATS || emptySeats < 0) {
                            System.err.println("Seat data inconsistency detected in flight: " + flightID);
                            confirmedSeats = Math.min(confirmedSeats, Flight.DEFAULT_MAX_SEATS);
                            emptySeats = Flight.DEFAULT_MAX_SEATS - confirmedSeats;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse seat numbers for flight: " + flightID);
                        confirmedSeats = 0;
                        emptySeats = Flight.DEFAULT_MAX_SEATS;
                    }

                    Flight flight = new Flight(flightID);
                    try {
                        flight.setConfirmedSeats(confirmedSeats);
                        flight.setEmptySeats(emptySeats);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid seat values for flight: " + flightID + ". Skipping this flight.");
                        continue;
                    }

                    List<Passenger> confirmedPassengersList = parsePassengersList(confirmedPassengersStr);
                    List<Passenger> waitlistedPassengersList = parsePassengersList(waitlistedPassengersStr);

                   flight.confirmedTicketList.addAll(confirmedPassengersList);
                   flight.waitingList.addAll(waitlistedPassengersList);

                    flightsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(flight);
                    flight.processWaitlist();

                     for (Passenger p : flight.confirmedTicketList) {
                        Ticket ticket = new Ticket(p, flight);
                         ticket.setStatus(Ticket.TicketStatus.CONFIRMED); // set the status here
                        p.addTicketToBucket(ticket);
                         tickets.put(ticket.getTicketId(), ticket); // Add the ticket to the map
                        passengers.put(p.getPassportNum(), p);
                    }
                    for (Passenger p : flight.waitingList) {
                         Ticket ticket = new Ticket(p, flight);
                         ticket.setStatus(Ticket.TicketStatus.WAITLISTED); // set the status here
                        p.addTicketToBucket(ticket);
                         tickets.put(ticket.getTicketId(), ticket);
                        passengers.put(p.getPassportNum(), p);
                    }


                } else {
                    System.err.println("CSV format error: insufficient fields in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV file: " + e.getMessage());
        }
    }

   // Retrieves passenger details based on the passport number
    public Passenger getPassengerInfo(String passportNum) {
        return passengers.get(passportNum);
    }
   // Retrieves ticket details based on the ticket ID
       public Ticket getTicketInfo(String ticketId) {
        return tickets.get(ticketId);
    }

    // Parses the passenger list from the string format.
   private List<Passenger> parsePassengersList(String passengersStr) {
        List<Passenger> passengerList = new ArrayList<>();
        if (passengersStr != null && !passengersStr.trim().isEmpty()) {
            String[] passengersArray = passengersStr.split(";"); // Changed variable name to avoid confusion
            for (String passengerStr : passengersArray) { // Changed variable name to avoid confusion
                passengerStr = passengerStr.trim();
                if (!passengerStr.isEmpty()) {
                    int startIndex = passengerStr.indexOf("(");
                    int endIndex = passengerStr.indexOf(")");

                    if (startIndex > 0 && endIndex > startIndex) {
                        String name = passengerStr.substring(0, startIndex).trim();
                        String passportNum = passengerStr.substring(startIndex + 1, endIndex).trim();

                        //Check if passenger exists, if not create a new one
                       Passenger passenger = passengers.get(passportNum);
                        if(passenger == null){
                            passenger = new Passenger(passportNum, name);
                            passengers.put(passportNum, passenger);
                        }
                        passengerList.add(passenger);
                    }
                }
            }
        }
        return passengerList;
    }
    // Saves flight data to the CSV file
   public void saveFlightsToCsv() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            writer.write("Flight ID,Date,Status,Confirmed Passengers," +
                    "Waitlisted Passengers,Confirmed Seats,Empty Seats,Waitlist Count\n");

            for (LocalDate date : flightsByDate.keySet()) {
                for (Flight flight : flightsByDate.get(date)) {
                    String flightID = flight.flightID;

                    String confirmedPassengersStr = getPassengerListString(flight.confirmedTicketList);
                    String waitlistedPassengersStr = getPassengerListString((ArrayList<Passenger>) flight.waitingList);

                    writer.write(flightID + "," + date + ",Available," +
                            confirmedPassengersStr + "," + waitlistedPassengersStr + "," +
                            flight.getConfirmedSeats() + "," + flight.getEmptySeats() + "," +
                            flight.getWaitlistCount() + "\n");
                }
            }
             System.out.println("Flight saved to csv");
        } catch (IOException e) {
            System.err.println("Failed to write CSV file: " + e.getMessage());
        }
    }

    // process the flight based on flightID
    public static Flight processFlightID(String flightID) {
         String[] parts = flightID.split("-");
        int flightIndex = Integer.parseInt(parts[1]);
        String dateString = parts[2] + "-" + parts[3] + "-" + parts[4];
        LocalDate date = LocalDate.parse(dateString, formatter);

        return flightsByDate.get(date).get(flightIndex);
    }

    // Searches flights within a date range and displays
    public static void searchFlights(LocalDate date1, LocalDate date2) {
      System.out.println("\nFlight for the Weeks: ");
        System.out.println();
        while (!date1.isAfter(date2)) {
            ArrayList<Flight> temp = flightsByDate.get(date1);
            if (temp != null) {
                for (int i = 0; i < temp.size(); i++) {
                    System.out.println(temp.get(i));
                }
            }
            System.out.println("-------------------------------------------------------------------------");
            date1 = date1.plusDays(1);
        }
    }

      // Converts a list of passengers to a string format
     private String getPassengerListString(ArrayList<Passenger> passengers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passengers.size(); i++) {
            Passenger passenger = passengers.get(i);
            sb.append(passenger.getName()).append("(").append(passenger.getPassportNum()).append(")");
            if (i < passengers.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
}
