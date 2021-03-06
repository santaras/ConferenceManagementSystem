package messaging;

import contact.ContactManager;

import java.util.*;

/**
 * Operations on Conversations
 */
public class ConversationController {
    private ConversationManager convoManager;
    private ContactManager contactManager;

    /**
     * Constructor for ConversationController
     * @param contactManager
     * @param conversationManager
     */
    public ConversationController(ContactManager contactManager, ConversationManager conversationManager) {
        this.contactManager = contactManager;
        this.convoManager = conversationManager;
    }

    /**
     * Checks if the User have access to send message to another User
     *
     * @param sender   UUID of the person who wants to send the message
     * @param receiver UUID of the person to whom the message is to be sent
     * @return true iff the receiver is in the friend list of sender
     */
    private boolean checkAccess(UUID sender, UUID receiver) {
        return contactManager.getContacts(sender) != null && contactManager.getContacts(sender).contains(receiver);
    }

    /**
     * Sends a particular message to a specific chat
     *
     * @param messageSender_id the Id of the sender of the message
     * @param messageContent   The content of the message to be sent
     * @param convId           the conversation Id of the conversation to which this message has to be added
     */
    public void sendMessage(UUID messageSender_id, String messageContent, UUID convId) {
        convoManager.sendMessage(messageSender_id, messageContent, convId);
    }

    /**
     * Initiates a new Chat(Conversation) between 2 or more users.
     *
     * @param convName         the name of the Chat to be initiated
     * @param executorUUID     the UUID of the user running this operation
     * @param otherUsers       the set of other users in this conversation
     * @param messageContent   The content of the initial message to be sent
     */
    public UUID initiateConversation(String convName, UUID executorUUID, Set<UUID> otherUsers, String messageContent) {
//        for (UUID otherUserUUID : otherUsers) {
//            if (!checkAccess(executorUUID, otherUserUUID)) {
//                throw new MessageDeniedException(executorUUID, otherUserUUID);
//            }
//        }

        // Give the executor user and the other users read and write permissions
        Set<UUID> conversationUsers = new HashSet<>(otherUsers);
        conversationUsers.add(executorUUID);

        return convoManager.createConversation(convName, conversationUsers, conversationUsers, executorUUID, messageContent);
    }

    /**
     * Gets messages for a conversation a user has read access to. Throws NoReadAccessException if the user has no
     * read access.
     *
     * @param userUUID         The ID of the User
     * @param conversationUUID The Id of the Conversation for which the messages need to be seen
     * @return returns an arraylist of Hashmaps. Each Hashmap stores information about a message in the conversation.
     */
    public ArrayList<Map<String, String>> getMessages(UUID userUUID, UUID conversationUUID) {
        return convoManager.getMessages(userUUID, conversationUUID);
    }

    /**
     * Get the conversation name
     *
     * @param conversationUUID UUID of the conversation to operate on
     * @return Conversation name
     */
    public String getConversationName(UUID conversationUUID) {
        return convoManager.getConversationName(conversationUUID);
    }


    /**
     * Get the set of Id's of Conversation that the user is part of
     *
     * @param userId UUID of the user for which the set of conversation is required
     * @return set of UUID's of conversations that the user is part of
     */
    public Set<UUID> getConversationlist(UUID userId) {
        return convoManager.getConversationlist(userId);
    }

    /**
     * Adds user to the a specific chat
     *
     * @param userUUID         The userId of the user to be added to the Chat
     * @param conversationUUID The UUID of the conversation/chat to which the user needs to be added
     */
    //public void addUser(UUID userUUID, UUID conversationUUID) {
    //    convoManager.addUser(userUUID, conversationUUID);
    //}
    // Not currently in use, need to add admin chat users for this

    /**
     * Adds user to the a specific chat
     *
     * @param userUUID         The userId of the user to be added to the Chat
     * @param conversationUUID The UUID of the conversation/chat to which the user needs to be added
     */
    //public void removeUser(UUID userUUID, UUID conversationUUID) {
    //    convoManager.removeUser(userUUID, conversationUUID);
    //}
    // Not currently in use, need to add admin chat users for this
}
