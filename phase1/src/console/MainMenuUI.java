package console;

import contact.ContactController;
import convention.ConferenceController;
import convention.EventController;
import convention.RoomController;
import messaging.ConversationController;
import user.UserController;

public class MainMenuUI {

    ConsoleUtilities consoleUtilities = new ConsoleUtilities();

    MessagingUI messagingUI;
    ContactsUI contactsUI;
    ConferencesUI conferencesUI;

    // User controller
    UserController userController;

    public MainMenuUI(UserController userController, ContactController contactController, ConversationController conversationController, RoomController roomController, EventController eventController, ConferenceController conferenceController) {
        this.userController = userController;

        this.messagingUI = new MessagingUI(conversationController);
        this.contactsUI = new ContactsUI(contactController);
        this.conferencesUI = new ConferencesUI(userController, roomController, eventController, conferenceController);
    }

    /**
     * Run the MainMenuUI
     *
     * @return true iff the user wants to quit the program
     */
    public boolean run() {

        String[] options = new String[]{
                "Messaging",
                "Contacts",
                "Conferences",
                String.format("Log Out (Signed in as %s)", userController.getUserFullName(userController.getCurrentUser())),
                "Exit System"
        };

        while (true) {
            int selection = consoleUtilities.singleSelectMenu("Welcome to our boi", "Cool system man", options);

            switch (selection) {
                case 1:
                    messagingUI.run();
                    break;
                case 2:
                    contactsUI.run();
                    break;
                case 3:
                    conferencesUI.run();
                    break;
                case 4:
                    userController.logout();
                    return false; // Logout (i.e. return to parent menu without terminating program)
                case 5:
                    return true; // Terminate program
            }
        }
    }
}