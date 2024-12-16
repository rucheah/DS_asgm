import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class GenerateFlightsCsv {
    // Main method to generate a CSV file for flights
    public static void main(String[] args) {
        // Define the file path and name for the CSV
        String filePath = "C:\\Users\\ruche\\OneDrive\\Documents\\";
        String fileName = "flights.csv";

        // HashMap to store flights by date
        HashMap<LocalDate, ArrayList<String>> flightsByDate = new HashMap<>();

        // Define the start and end dates for flight generation
        LocalDate startDate = LocalDate.of(2024, 12, 5);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        // Loop through each date to create flight IDs
        while (!startDate.isAfter(endDate)) {
            ArrayList<String> dailyFlights = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                String flightID = "Flight-" + i + "-" + startDate;
                dailyFlights.add(flightID);
            }
            flightsByDate.put(startDate, dailyFlights);
            startDate = startDate.plusDays(1);
        }

        // Write the flight data to the CSV file
        try (FileWriter writer = new FileWriter(filePath + fileName)) {
            // Write the CSV header
            writer.write("Flight ID,Date,Status,Confirmed Passengers," +
                    "Waitlisted Passengers,Confirmed Seats,Empty Seats,Waitlist Count\n");

            // Loop through each date and flight ID to write a CSV row
            for (LocalDate date : flightsByDate.keySet()) {
                for (int i = 0; i < 3; i++) {
                    String flightID = "Flight-" + i + "-" + date;

                    writer.write(flightID + "," + date + ",Available," +
                            "" + "," + "" + "," +
                            0 + "," + 5 + "," + 0 + "\n");
                }
            }

            System.out.println("The CSV file was generated:" + filePath + fileName);
        } catch (IOException e) {
            System.err.println("Failed to write CSV file: " + e.getMessage());
        }
    }
}
