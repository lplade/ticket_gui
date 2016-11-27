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
    private JComboBox newPriorityComboBox;
    private JTable ticketsTable;
    private JButton closeSelectedTicketButton;
    private JButton addNewTicketButton;

    Vector<Ticket> ticketQueue;
    Vector<ResolvedTicket> resolvedList;

    private DefaultTableModel tableModel;

    TicketGUI() throws IOException {

        //set up date formatter
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date todaysDate = new Date();

        //set up file reading
        String filename = "open_tickets.txt";
        File f = new File(filename);
        String filename2 = "Resolved_tickets_as_of_" + dateFormat.format(todaysDate) + ".txt";

        //import saved data if possible
        if (f.isFile()){
            ticketQueue = FileIO.readTickets(filename);
        } else {
            System.out.println("No file found. Starting new issue tracker.");
            ticketQueue = new Vector<>();
        }

        setContentPane(rootPanel);
        setPreferredSize(new Dimension(500, 500));

        this.tableModel = new DefaultTableModel();

        setupTableModel(importedTickets);

        this.ticketsTable.setModel(tableModel);

        setupComboBox();

        addListeners(importedTickets, resolvedList);

        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // We need to save the files out before we exit
        //http://stackoverflow.com/questions/13800621/call-a-method-when-application-closes
        //setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);


        setVisible(true);

    }

    private void setupTableModel(LinkedList<Ticket> importedTickets) {
        //https://gilbertadjin.wordpress.com/2009/05/05/populating-a-jtable-with-a-collection-list/

        //Add column headings
        String[] tableColumnNames = new String[5];
        tableColumnNames[0] = "ID";
        tableColumnNames[1] = "Priority";
        tableColumnNames[2] = "Description";
        tableColumnNames[3] = "Reported by";
        tableColumnNames[4] = "Date reported";
        tableModel.setColumnIdentifiers(tableColumnNames);

        //Map imported LinkedList (if any) to TableModel
        if (importedTickets == null) {
            return;
        }


        for (Ticket ticket : importedTickets) {
            addToTableModel(ticket);
        }


    }

    private void addToTableModel(Ticket newTicket) {
        Object[] tableRow = new Object[5];

        //put each element of Table object into array
        tableRow[0] = newTicket.getTicketID();
        tableRow[1] = newTicket.getPriority();
        tableRow[2] = newTicket.getDescription();
        tableRow[3] = newTicket.getReporter();
        tableRow[4] = newTicket.getDateReportedString();

        //add the array to the model
        tableModel.addRow(tableRow);
    }


    private void setupComboBox() {
        //put numbers 1-5 in the combo box
        for (int r = 1; r <= 5; r++) newPriorityComboBox.addItem(r);
    }

    private void addListeners(LinkedList<Ticket> importedTickets, LinkedList<ResolvedTicket> resolvedList) {
        //listen for clicked buttons

        //When we click the "Mark selected ticket as resolved" button...
        //http://stackoverflow.com/questions/23465295/remove-a-selected-row-from-jtable-on-button-click
        closeSelectedTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ticketsTable.getSelectedRow();
                if (selectedRow != -1){
                    //figure out the ID of the selected ticket
                    int deleteID = (int) ticketsTable.getValueAt(selectedRow, 0);
                    //remove the row from the model
                    tableModel.removeRow(selectedRow);
                    //loop through the LinkedList and delete the ticket with that ID

                    for (Ticket ticket : importedTickets) {
                        if (ticket.getTicketID() == deleteID) {

                            //interactive prompt for resolution info
                            //TODO implement
                            Date dateResolved = new Date(); //defaults to today

                            //Instance a new resolved ticket
                            //String desc, int p, String rep, Date dateInit, int id, String res, Date dateRes
                            ResolvedTicket rt = new ResolvedTicket(
                                    ticket.getDescription(),
                                    ticket.getPriority(),
                                    ticket.getReporter(),
                                    ticket.getDateReported(),
                                    ticket.getTicketID(),
                                    resInput,
                                    dateResolved
                            );

                            //add the old ticket into the resolved list
                            resolvedList.add(rt);

                            //then purge the ticket from the active list
                            ticketQueue.remove(ticket);
                            System.out.println(String.format("Ticket %d deleted", deleteID));
                            break; //don't need loop any more.
                        }
                    }
                }
                //TODO remove ticket from tableModel
                //TODO generate a ResolvedTicket
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
                int rating = (int)newPriorityComboBox.getSelectedItem();

                //TODO do something with these
            }
        });

        WindowListener windowListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
            }
        };

    }




}

