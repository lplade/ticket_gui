package name.lplade;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) throws IOException {

        //set up date formatter
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date todaysDate = new Date();

        //set up file reading
        String filename = "open_tickets.txt";
        File f = new File(filename);
        String filename2 = "Resolved_tickets_as_of_" + dateFormat.format(todaysDate) + ".txt";

        LinkedList<Ticket> ticketQueue = new LinkedList<Ticket>();
        LinkedList<ResolvedTicket> resolvedList = new LinkedList<ResolvedTicket>();

        //read open_tickets.txt
        if (f.isFile()){
            ticketQueue = readTickets(filename);
        } else {
            System.out.println("No file found. Starting new issue tracker.");
        }

        //instantiate the GUI
        TicketGUI ticketGUI = new TicketGUI(ticketQueue, resolvedList);
    }


    //read the tickets in from a file
    public static LinkedList<Ticket> readTickets(String filename) throws IOException {

        int highestInt = 1; //keep track of where we are in the Ticket global index

        //initialize a LinkedList
        LinkedList<Ticket> ticketList = new LinkedList<Ticket>();

        //open the file specified
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))){
            String delim = Character.toString(Ticket.DELIM);

            //read the first line
            String line = bufferedReader.readLine();

            //System.out.println("DEBUG: line is " + line);

            //if not null, parse it
            while (line != null) {
                // 1|Fire|5|Bob|1478583669804
                String tokens[] = line.split(delim);

                //TODO more validation in case user tampers with input file
                int ticketID = Integer.parseInt(tokens[0]);
                String description = tokens[1];
                int priority = Integer.parseInt(tokens[2]);
                String reporter = tokens[3];
                //http://stackoverflow.com/questions/535004/unix-epoch-time-to-java-date-object
                Date dateReported = new Date(Long.parseLong(tokens[4]));

                highestInt = ticketID;

                //Store these in the LinkedList
                //Note second form of constructor used
                ticketList.add(new Ticket(description, priority, reporter, dateReported, ticketID));

                //then read the next line
                line = bufferedReader.readLine();

            }

            bufferedReader.close(); //close the file when done
        } catch (IOException ioe) {
            System.out.println("Error reading " + filename);
            System.out.println(ioe.toString());
            System.exit(1);
        }

        //write directly to the Ticket class to update the counter
        //maybe not the greatest practice in the world
        Ticket.setTicketIDCounter(highestInt + 1);
        return ticketList;
    }

    // *** Helper methods ***

    public static void storeTickets(LinkedList<Ticket> ticketQueue, String filename) throws IOException {
        System.out.print("Writing tickets to " + filename + "... ");

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {

            for (Ticket t : ticketQueue ) {
                //call the method that formats a ticket. remember to add a newline
                String outString = t.toStringDelimited() + "\n";
                //write the line
                bufferedWriter.write(outString);
            }

            //close the writer when done
            bufferedWriter.close();
            System.out.println("Done!");
        } catch (IOException e) {
            System.out.println("Error writing to " + filename);
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public static void storeResolvedTickets(LinkedList<ResolvedTicket> ticketQueue, String filename) throws IOException {
        System.out.print("Archiving resolved tickets to " + filename + "... ");

        //Is there any way to not duplicate above code here? Need to cast method to Ticket OR ResolvedTicket, but all else is identical.

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {

            for (Ticket t : ticketQueue ) {
                //call the method that formats a ticket. remember to add a newline
                String outString = t.toStringDelimited() + "\n";
                //write the line
                bufferedWriter.write(outString);
            }

            //close the writer when done
            bufferedWriter.close();
            System.out.println("Done!");
        } catch (IOException e) {
            System.out.println("Error writing to " + filename);
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }

}