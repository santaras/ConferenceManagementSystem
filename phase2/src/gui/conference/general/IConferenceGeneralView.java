package gui.conference.general;

import gui.util.interfaces.IPanel;

import javax.swing.table.TableModel;
import java.util.UUID;

public interface IConferenceGeneralView {
    void setTableData(String[][] tableData, String[] columnNames);
}