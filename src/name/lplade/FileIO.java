package name.lplade;

import java.io.*;
import java.util.Date;
import java.util.Vector;

public class FileIO  {


    //read the tickets in from a file
    static Vector<Ticket> readTickets(String filename) throws IOException {

        int highestInt = 1; //keep track of where we are in the Ticket global index

        //initialize a Vector
        Vector<Ticket> ticketList = new Vector<>();

        //open the file specified
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))){
            String delim = Character.toString(Ticket.DELIM);

            //read the first line
            String line = bufferedReader.readLine();

            //System.out.println("DEBUG: line is " + line);

            //if not null, parse it
            while (line != null) {
                // 1;Fire;5;Bob;1478583669804
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
            //This crashes program... make sure to test if the file exists first before calling
            System.exit(1);
        }

        //write directly to the Ticket class to update the counter
        //maybe not the greatest practice in the world
        Ticket.setTicketIDCounter(highestInt + 1);
        return ticketList;

    }


    static void storeTickets(Vector<Ticket> ticketQueue, String filename) throws IOException {
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

    static void storeResolvedTickets(Vector<ResolvedTicket> ticketQueue, String filename) throws IOException {
        System.out.print("Writing tickets to " + filename + "... ");

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {

            for (ResolvedTicket t : ticketQueue ) {
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
