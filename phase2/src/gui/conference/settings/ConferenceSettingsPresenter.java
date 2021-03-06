package gui.conference.settings;

import convention.exception.LoneOrganizerException;
import gui.conference.tabs.ConferenceTabsConstants;
import gui.conference.util.AbstractConferencePresenter;
import gui.util.enums.DialogFactoryOptions;
import gui.util.enums.PanelFactoryOptions;
import gui.util.interfaces.IDialog;
import gui.util.interfaces.IFrame;

import java.util.*;
import java.util.function.Function;

/**
 * Manages ConferenceSettingsView
 */
class ConferenceSettingsPresenter extends AbstractConferencePresenter {

    private IConferenceSettingsView conferenceSettingsView;

    /**
     * @param mainFrame              main GUI frame
     * @param conferenceSettingsView view being managed
     * @param conferenceUUID         UUID of the associated conference
     */
    ConferenceSettingsPresenter(IFrame mainFrame, IConferenceSettingsView conferenceSettingsView, UUID conferenceUUID) {
        super(mainFrame, conferenceUUID);

        this.conferenceSettingsView = conferenceSettingsView;

        updateConferenceUsers();
    }

    /**
     * Updates the table of conference users
     */
    private void updateConferenceUsers() {
        List<UUID> userUUIDs = new ArrayList<>(conferenceController.getUsers(conferenceUUID, signedInUserUUID));

        String[] columnNames = {
                "First Name",
                "Last Name",
                "Username",
                "UUID",
                "Attendee",
                "Speaker",
                "Organizer",
                "God"
        };

        String[][] tableData = new String[userUUIDs.size()][columnNames.length];

        for (int i = 0; i < userUUIDs.size(); i++) {
            UUID targetUserUUID = userUUIDs.get(i);

            tableData[i] = new String[]{
                    userController.getUserFirstName(targetUserUUID),
                    userController.getUserLastName(targetUserUUID),
                    userController.getUserUsername(targetUserUUID),
                    targetUserUUID.toString(),
                    conferenceController.isAttendee(conferenceUUID, targetUserUUID, targetUserUUID) ? "YES" : "NO",
                    conferenceController.isSpeaker(conferenceUUID, targetUserUUID, targetUserUUID) ? "YES" : "NO",
                    conferenceController.isOrganizer(conferenceUUID, targetUserUUID, targetUserUUID) ? "YES" : "NO",
                    userController.getUserIsGod(targetUserUUID) ? "YES" : "NO"
            };
        }

        conferenceSettingsView.setUserList(tableData, columnNames);
    }

    /**
     * Creates a conversation with conference members
     */
    void createConversation() {
        IDialog chooseUsersDialog = dialogFactory.createDialog(DialogFactoryOptions.dialogNames.MULTI_USER_PICKER, new HashMap<String, Object>() {
            {
                put("instructions", "Select users to add to the new conversation");
                put("availableUserUUIDs", conferenceController.getUsers(conferenceUUID, signedInUserUUID));
            }
        });

        Set<UUID> selectedUserUUIDs = (Set<UUID>) chooseUsersDialog.run();

        if (selectedUserUUIDs != null) {
            selectedUserUUIDs.add(signedInUserUUID); // We need to add the signed in user in the conversation too

            UUID conversationUUID = conferenceController.createConversationWithUsers(conferenceUUID, signedInUserUUID, selectedUserUUIDs);

            IDialog conversationCreatedDialog = dialogFactory.createDialog(DialogFactoryOptions.dialogNames.MESSAGE, new HashMap<String, Object>() {
                {
                    put("message", String.format("Conversation with %d users created successfully (%s)", selectedUserUUIDs.size(), conversationUUID));
                    put("title", "Conversation created");
                    put("messageType", DialogFactoryOptions.dialogType.INFORMATION);
                }
            });

            conversationCreatedDialog.run();

            // Open the new conversation
            openConversation(conversationUUID);
        }
    }

    /**
     * Opens the messages tab to a given conversation UUID
     *
     * @param conversationUUID UUID of conversation to open
     */
    private void openConversation(UUID conversationUUID) {
        mainFrame.setPanel(panelFactory.createPanel(PanelFactoryOptions.panelNames.MAIN_MENU, new HashMap<String, Object>() {
            {
                put("defaultConversationUUID", conversationUUID);
                put("defaultTabIndex", 1);
            }
        }));
    }

    /**
     * Method to start a dialog to select a user and confirm selection. Checks if there are actually users available,
     * and executes lambda function on success.
     *
     * @param availableUserUUIDs      set of user UUIDs to be available for selection
     * @param title                   dialog title
     * @param instructions            instructions for the user selection menu
     * @param emptyListMessage        message displayed if there are no available users
     * @param confirmMessageGenerator lambda function to generate confirm message
     * @param successMessageGenerator lambda function to generate success message
     * @param submit                  lambda function to execute on submit
     * @return UUID of the user select, or null if operation was unsuccessful
     */
    private UUID confirmSelectUser(Set<UUID> availableUserUUIDs, String title, String instructions, String emptyListMessage, Function<UUID, String> confirmMessageGenerator, Function<UUID, String> successMessageGenerator, Function<UUID, String> submit) {
        if (availableUserUUIDs.size() == 0) {
            IDialog noUsersAvailableDialog = dialogFactory.createDialog(DialogFactoryOptions.dialogNames.MESSAGE, new HashMap<String, Object>() {
                {
                    put("message", emptyListMessage);
                    put("title", title);
                    put("messageType", DialogFactoryOptions.dialogType.ERROR);
                }
            });

            noUsersAvailableDialog.run();

        } else {
            IDialog organizerPickerDialog = dialogFactory.createDialog(DialogFactoryOptions.dialogNames.USER_PICKER, new HashMap<String, Object>() {
                {
                    put("availableUserUUIDs", availableUserUUIDs);
                    put("instructions", instructions);
                }
            });

            UUID selectedUserUUID = (UUID) organizerPickerDialog.run();

            if (selectedUserUUID != null) {
                IDialog confirmSelection = dialogFactory.createDialog(DialogFactoryOptions.dialogNames.CONFIRM_BOOLEAN, new HashMap<String, Object>() {
                    {
                        put("message", confirmMessageGenerator.apply(selectedUserUUID));
                        put("title", title);
                        put("messageType", DialogFactoryOptions.dialogType.WARNING);
                        put("confirmationType", DialogFactoryOptions.optionType.YES_NO_OPTION);
                    }
                });

                if ((boolean) confirmSelection.run()) {

                    // Run the function on submit
                    String errorMessage = submit.apply(selectedUserUUID);

                    if (errorMessage == null) {
                        IDialog successDialog = dialogFactory.createDialog(DialogFactoryOptions.dialogNames.MESSAGE, new HashMap<String, Object>() {
                            {
                                put("message", successMessageGenerator.apply(selectedUserUUID));
                                put("title", title);
                                put("messageType", DialogFactoryOptions.dialogType.INFORMATION);
                            }
                        });

                        successDialog.run();

                        return selectedUserUUID;
                    } else {
                        IDialog failDialog = dialogFactory.createDialog(DialogFactoryOptions.dialogNames.MESSAGE, new HashMap<String, Object>() {
                            {
                                put("message", errorMessage);
                                put("title", title);
                                put("messageType", DialogFactoryOptions.dialogType.ERROR);
                            }
                        });

                        failDialog.run();
                    }
                }
            }
        }

        return null;
    }

    /**
     * Add a user as an organizer
     */
    void addOrganizer() {
        // Users that are eligible to become organizers are in the system, but are not already organizers
        Set<UUID> availableUserUUIDs = new HashSet<>(userController.getUsers());
        availableUserUUIDs.removeAll(conferenceController.getOrganizers(conferenceUUID, signedInUserUUID));

        confirmSelectUser(
                availableUserUUIDs,
                "Add Organizer",
                "Choose a user to give organizer permissions to. The user will be invited to the conference if they are not already a member.",
                "There are no users available to add as an organizer.",
                (uuid) -> String.format("Are you sure you want to invite %s (%s) to be an organizer? They will have full access to this conference.", userController.getUserFullName(uuid), uuid),
                (uuid) -> String.format("%s (%s) has been successfully granted organizer permissions.", userController.getUserFullName(uuid), uuid),
                (uuid) -> {
                    conferenceController.addOrganizer(conferenceUUID, signedInUserUUID, uuid);
                    return null; // There is no fail message
                }
        );

        updateConferenceUsers();
    }

    /**
     * Remove a user from thee conference
     */
    void removeUser() {
        Set<UUID> conferenceUserUUIDs = new HashSet<>(conferenceController.getUsers(conferenceUUID, signedInUserUUID));

        UUID removedUserUUID = confirmSelectUser(
                conferenceUserUUIDs,
                "Remove user",
                "Choose a user to remove from the conference. All of their roles, events registrations, and speaker assignments will be cancelled.",
                "There are no users to remove from the conference. (wait how did this even happen? are you god?)",
                (uuid) -> String.format("Are you sure you want to remove %s (%s) from the conference?", userController.getUserFullName(uuid), uuid),
                (uuid) -> String.format("%s (%s) has been removed from the conference.", userController.getUserFullName(uuid), uuid),
                (uuid) -> {
                    try {
                        conferenceController.leaveConference(conferenceUUID, signedInUserUUID, uuid);
                    } catch (LoneOrganizerException e) {
                        return e.toString();
                    }

                    return null; // There is no fail message
                }
        );

        if (removedUserUUID == signedInUserUUID) {
            // Send user back to main menu since they're not in the conference anymore
            loadMainMenu();
        } else {
            // Reload the main menu to update page and stats
            reloadSettingsPage();
        }
    }

    /**
     * Remove a user's organizer permissions
     */
    void removeOrganizer() {
        Set<UUID> conferenceUserUUIDs = new HashSet<>(conferenceController.getOrganizers(conferenceUUID, signedInUserUUID));

        UUID removedUserUUID = confirmSelectUser(
                conferenceUserUUIDs,
                "Remove organizer",
                "Choose a user to revoke organizer permissions from. If they do not have any other roles, they will also be removed from the conference.",
                "There are no users to remove as an organizer. (wait how did this even happen? are you god?)",
                (uuid) -> String.format("Are you sure you want to remove %s (%s) from the conference?", userController.getUserFullName(uuid), uuid),
                (uuid) -> String.format("%s (%s) is no longer an organizer.", userController.getUserFullName(uuid), uuid),
                (uuid) -> {
                    try {
                        conferenceController.leaveConference(conferenceUUID, signedInUserUUID, uuid);
                    } catch (LoneOrganizerException e) {
                        return e.toString();
                    }

                    return null; // There is no fail message
                }
        );

        if (removedUserUUID == signedInUserUUID) {
            // Send the user back to the general conference page, since this user isn't an organizer anymore
            mainFrame.setPanel(panelFactory.createPanel(PanelFactoryOptions.panelNames.MAIN_MENU, new HashMap<String, Object>() {
                {
                    put("defaultConferenceUUID", conferenceUUID);
                }
            }));
        } else {
            // Reload the main menu to update page and stats
            reloadSettingsPage();
        }
    }

    /**
     * Invites a user as an attendee
     */
    void addAttendee() {
        // Users that are eligible to become attendees are in the system, but are not already attendees
        Set<UUID> availableUserUUIDs = new HashSet<>(userController.getUsers());
        availableUserUUIDs.removeAll(conferenceController.getAttendees(conferenceUUID, signedInUserUUID));

        confirmSelectUser(
                availableUserUUIDs,
                "Add Attendee",
                "Choose a user to invite as an attendee.",
                "There are no users available to add as an attendee.",
                (uuid) -> String.format("Are you sure you want to invite %s (%s) to be an attendee?", userController.getUserFullName(uuid), uuid),
                (uuid) -> String.format("%s (%s) has been invited as an attendee.", userController.getUserFullName(uuid), uuid),
                (uuid) -> {
                    conferenceController.addAttendee(conferenceUUID, uuid);
                    return null; // There is no fail message
                }
        );

        // Reload the main menu to update page and stats
        reloadSettingsPage();
    }

    /**
     * Deletes a conference
     */
    void deleteConference() {
        IDialog confirmDeleteDialog = dialogFactory.createDialog(DialogFactoryOptions.dialogNames.CONFIRM_BOOLEAN, new HashMap<String, Object>() {
            {
                put("message", String.format("Are you sure you want to DELETE this conference? You CANNOT undo this. (%s)", conferenceController.getConferenceName(conferenceUUID)));
                put("title", "Confirm delete conference");
                put("messageType", DialogFactoryOptions.dialogType.WARNING);
                put("confirmationType", DialogFactoryOptions.optionType.YES_NO_OPTION);
            }
        });

        if ((Boolean) confirmDeleteDialog.run()) {
            conferenceController.deleteConference(conferenceUUID, signedInUserUUID);

            // Reload the main menu to update changes
            loadMainMenu();
        }
    }

    /**
     * Edits details about a conference
     */
    void editConference() {
        IDialog conferenceFormDialog = dialogFactory.createDialog(DialogFactoryOptions.dialogNames.CONFERENCE_FORM, new HashMap<String, Object>() {
            {
                put("conferenceUUID", conferenceUUID);
            }
        });

        if (conferenceFormDialog.run() != null) {
            // Reload the main menu to update changes
            reloadSettingsPage();
        }
    }

    /**
     * Return to main menu
     */
    private void loadMainMenu() {
        mainFrame.setPanel(panelFactory.createPanel(PanelFactoryOptions.panelNames.MAIN_MENU));
    }

    /**
     * Reload page and select current conference and tab
     */
    private void reloadSettingsPage() {
        mainFrame.setPanel(panelFactory.createPanel(PanelFactoryOptions.panelNames.MAIN_MENU, new HashMap<String, Object>() {
            {
                put("defaultConferenceUUID", conferenceUUID);
                put("defaultTabName", ConferenceTabsConstants.tabNames.SETTINGS);
            }
        }));
    }
}
