package console;

import contact.ContactController;
import convention.ConferenceController;
import convention.EventController;
import convention.RoomController;
import messaging.ConversationController;
import user.UserController;

public class MainMenuUI {
    // User controller
    UserController userController;

    // Messaging controllers
    ContactController contactController;
    ConversationController conversationController;

    // Convention controllers
    RoomController roomController;
    EventController eventController;
    ConferenceController conferenceController;

    public MainMenuUI(UserController userController, ContactController contactController, ConversationController conversationController, RoomController roomController, EventController eventController, ConferenceController conferenceController) {
        this.userController = userController;
        this.contactController = contactController;
        this.conversationController = conversationController;
        this.roomController = roomController;
        this.eventController = eventController;
        this.conferenceController = conferenceController;
    }

    public boolean run() {
        String[] options = new String[]{
                "asd",
                "asdasdas",
                "asdsad"
        };

        ConsoleUtilities consoleUtilities = new ConsoleUtilities();
        int selection = consoleUtilities.singleSelectMenu("Welcome to our boi", "Cool system man", options);

        switch (selection) {
            case 1:
                consoleUtilities.confirmBoxClear("u made the wrong choice");
                break;
            default:
                consoleUtilities.confirmBoxClear("hi there stranger");
        }
    }
}
