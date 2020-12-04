package gui.contacts;

import gui.util.interfaces.IFrame;
import gui.util.interfaces.IPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ContactsView implements IPanel, IContactsView {
    private JPanel contactsPanel;
    private JButton requestsButton;
    private JButton deleteContactsButton;
    private JButton sendRequestsButton;
    private JList contactsList;
    private ContactsPresenter contactsPresenter;

    public ContactsView(IFrame mainFrame) {

        requestsButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the events to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {


            }
        });
        sendRequestsButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the events to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                contactsPresenter.sendRequest();
            }
        });
        deleteContactsButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the events to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    @Override
    public JPanel getPanel() {
        return contactsPanel;
    }

    @Override
    public void setContactsList(String[] contacts) {
    }
}
