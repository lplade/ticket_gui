package name.lplade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

public class TicketGUI extends JFrame {

    private JPanel rootPanel;
    private JTextField newDescriptionTextField;
    private JTextField newReporterTextField;
    private JComboBox<Integer> newPriorityComboBox;
    private JTable ticketsTable;
    private JButton closeSelectedTicketButton;
    private JButton addNewTicketButton;
    private JTextField newResolutionTextField;

    Vector<Ticket> ticketQueue;
    Vector<ResolvedTicket> resolvedList;

    private String filename;
    private String filename2;

    private TicketTableModel tableModel;


    //CONSTRUCTOR
    TicketGUI() throws IOException {

        super("Support Tickets");

        //set up date formatter
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date todaysDate = new Date();

        //set up file reading
        filename = "open_tickets.txt";
        File f = new File(filename);
        filename2 = "Resolved_tickets_as_of_" + dateFormat.format(todaysDate) + ".txt";

        //import saved data if possible
        if (f.isFile()){
            ticketQueue = FileIO.readTickets(filename);
        } else {
            System.out.println("No file found. Starting new issue tracker.");
            ticketQueue = new Vector<>();
        }
        //initialize the archive collection
        resolvedList = new Vector<>();

        setContentPane(rootPanel);
        setPreferredSize(new Dimension(800, 500));

        tableModel = new TicketTableModel(ticketQueue); //provide Vector of tickets


        //setupTableModel(importedTickets);

        ticketsTable.setModel(tableModel);

        //Configure JTable to only permit selection of single row
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //put numbers 1-5 in the priority combobox
        setupComboBox();

        addListeners();

        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setVisible(true);

    }

    //methods split from constructor to make code more concise

    private void setupComboBox() {
        //put numbers 1-5 in the combo box
        for (int r = 1; r <= 5; r++) newPriorityComboBox.addItem(r);
    }

    private void addListeners() {
        //listen for clicked buttons

        //When we click the "Mark selected ticket as resolved" button...
        //http://stackoverflow.com/questions/23465295/remove-a-selected-row-from-jtable-on-button-click
        closeSelectedTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //look in newResolutionTextField
                String resolution = newResolutionTextField.getText();
                //validate this
                if (! testStringNotNull(resolution, "resolution")) return;

                //use current date as resolved date
                Date resolvedDate = new Date();

                //This SHOULD retrieve the ticket from the model corresponding to the selected row
                Ticket ticketToClose = tableModel.getTicketAtRow(ticketsTable.getSelectedRow());

                //make the new ticket to archive
                ResolvedTicket resolvedTicket = new ResolvedTicket(
                        ticketToClose.getDescription(),
                        ticketToClose.getPriority(),
                        ticketToClose.getReporter(),
                        ticketToClose.getDateReported(),
                        ticketToClose.getTicketID(),
                        resolution,
                        resolvedDate
                );

                //add to vector
                resolvedList.addElement(resolvedTicket);

                //remove the old ticket from the first vector
                ticketQueue.removeElement(ticketToClose);

                //tell JTable to refresh
                tableModel.fireTableDataChanged();

            }
        });

        //When we click the "Add new ticket" button...
        addNewTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //newDescriptionTextField
                String description = newDescriptionTextField.getText();
                //newReporterTextField
                String reporter = newReporterTextField.getText();
                //newPriorityComboBox
                int priority = (int)newPriorityComboBox.getSelectedItem();
                Date dateReported = new Date(); //Default constructor creates date with current date/time

                //validate these
                if (! testStringNotNull(description, "description")) return;
                if (! testStringNotNull(reporter, "name of the person reporting")) return;
                assert (priority >= 1 && priority <= 5);
                //TODO more tests?

                //if validation passes
                Ticket newTicket = new Ticket(description, priority, reporter, dateReported);

                //add the new ticket to the Vector
                ticketQueue.addElement(newTicket);

                //tell JTable to refresh
                tableModel.fireTableDataChanged();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(
                        TicketGUI.this,
                        "Are you sure you want to save and exit?",
                        "Exit?",
                        JOptionPane.OK_CANCEL_OPTION)) {
                    //Save all of the data...
                    try {
                        FileIO.storeTickets(ticketQueue, filename);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        FileIO.storeResolvedTickets(resolvedList, filename2);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    //And quit.
                    System.exit(0);
                }
                //super.windowClosing(e);
            }
        });


    }


    //helper methods

    private boolean testStringNotNull(String inString, String fieldName) {
        // inString is the string to test
        // fieldName is the word displayed to the user in the error
        if ( inString == null || inString.length() == 0) {
            String errMsg = "Please enter the " + fieldName + " for this issue";
            JOptionPane.showMessageDialog(
                    TicketGUI.this,
                    errMsg
            );
            return false;
        } else {
            return true;
        }
    }

}

