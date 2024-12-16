import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static AnnualFlightScheduler afs;
    private static Scanner sc; // Single scanner for input

    public static void main(String[] args) {
        afs = new AnnualFlightScheduler();
        sc = new Scanner(System.in); // Initialize the scanner
        int choice;

        do {
            System.out.println("\nWelcome to Flight Booking System");
            System.out.print("""
                    MENU:
                    (1): Search Flight
                    (2): Book Ticket
                    (3): Edit Ticket Information
                    (4): View Ticket Status
                    (5): Cancel a Ticket
                    (6): Exit
                                                            
                    Enter your choice here:  """);

             try {
                 choice = sc.nextInt();
             }
            catch(InputMismatchException e){
                 System.err.println("Invalid input, please enter a number between 1 and 6");
                 sc.nextLine();
                 choice = 0;
                 continue;
            }
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1: {
                    Main.SearchFlightForWeek();
                    break;
                }
                case 2: {
                    Main.BookTicket();
                    break;
                }
                case 3:
                    Main.EditTicketInformation();
                    break;
                case 4: {
                    Main.ViewTicketStatus();
                    break;
                }
                case 5: {
                    Main.CancelTicket();
                    break;
                }
                case 6: {
                    System.out.println("Thanks for using!");
                    afs.saveFlightsToCsv();
                    break; // Exit the loop for option 6
                }
                default: {
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }

            }
        } while (choice != 6);

        sc.close(); // Close scanner before exiting
    }

     public static void EditTicketInformation(){
        System.out.print("Enter your current passport number: ");
        String passportNum = sc.nextLine();

        Passenger passenger = afs.getPassengerInfo(passportNum);

        if(passenger != null){
            System.out.print("Enter new name (press Enter to skip): ");
            String newName = sc.nextLine();
            System.out.print("Enter new passport number (press Enter to skip): ");
            String newPassportNum = sc.nextLine();

            passenger.editPassengerDetails(newName, newPassportNum);
            System.out.print("Passenger details updated successfully.");
        }else{
            System.out.print("Passenger not found.");
        }
    }

    public static void BookTicket() {

        System.out.print("Enter the flight ID (format: Flight-<number>-<yyyy-MM-dd>): ");
        String flightID = sc.nextLine();
        System.out.print("Please enter your name: ");
        String name = sc.nextLine();
        System.out.print("Please enter your passport number: ");
        String passportNum = sc.nextLine();

        Flight flight = AnnualFlightScheduler.processFlightID(flightID);

         Passenger passenger = Main.afs.getPassengerInfo(passportNum);

         if(passenger == null) {
           passenger = new Passenger(passportNum, name);
            Main.afs.passengers.put(passportNum,passenger); //add to the list
        }

        flight.bookFlight(passenger, flight);
    }


      public static void ViewTicketStatus() {
        System.out.print("Please enter your passport number: ");
        String passportNum = sc.nextLine();

        Passenger passenger = afs.getPassengerInfo(passportNum);

          if (passenger != null) {
                passenger.viewTicketStatus();
          } else {
                System.out.print("The passenger information could not be found.");
            }
    }


    public static void CancelTicket(){
        System.out.print("Enter the flight ID: ");
        String flightID = sc.nextLine();
        System.out.print("Enter your passport number: ");
        String passportNum = sc.nextLine();

         Flight flight = AnnualFlightScheduler.processFlightID(flightID);
        Passenger passenger = afs.getPassengerInfo(passportNum);

        if(passenger != null && flight != null){
            boolean result = flight.cancelTicket(passenger);
            if(result){
                System.out.println("Ticket successfully canceled.");
            }else{
                System.out.println("Failed to cancel the ticket. Please check your information.");
            }
        }else{
            System.out.println("Flight or passenger not found.");
        }
    }

    public static void SearchFlightForWeek(){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.print("Enter a starting date: ");
        LocalDate date1;
        LocalDate date2;

      try {
          date1 = LocalDate.parse(sc.nextLine(), formatter);
          System.out.print("Enter an ending date: ");
          date2 = LocalDate.parse(sc.nextLine(), formatter);
           AnnualFlightScheduler.searchFlights(date1,date2);
      }
      catch(Exception e){
          System.err.println("Invalid date format, please enter the date in yyyy-MM-dd format");
      }
    }
}