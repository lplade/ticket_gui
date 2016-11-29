package name.lplade;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

//Based on Clara lake_run_gui example

public class TicketTableModel extends AbstractTableModel {

    private Vector<Ticket> allTickets;

    // Column names, displayed as table headers in the JTable.

    private String[] columnNames = {
            "ID",
            "Priority",
            "Description",
            "Reported by",
            "Date reported"
    };

    TicketTableModel(Vector<Ticket> tickets) {
        allTickets = tickets;
    }

    @Override
    public int getRowCount() {
        return allTickets.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: // ID
                return allTickets.get(rowIndex).getTicketID();
            case 1: // Priority
                return allTickets.get(rowIndex).getPriority();
            case 2: // Description
                return allTickets.get(rowIndex).getDescription();
            case 3: // Reported by
                return allTickets.get(rowIndex).getReporter();
            case 4: // Date reported
                return allTickets.get(rowIndex).getDateReportedString();
            default: //should never get here
                return null;
        }
    }

    public Ticket getTicketAtRow(int rowIndex){
        return allTickets.get(rowIndex);
    }

    @Override
    public String getColumnName(int col){
        return columnNames[col];
    }


}
