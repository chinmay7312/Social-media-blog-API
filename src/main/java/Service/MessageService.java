package Service;

import java.sql.SQLException;
import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO;

    // new MessageService with new MessageDAO
    public MessageService() {
        messageDAO = new MessageDAO();
    }

    /*
     * Function: create a new message to store in database
     * Use MessageDAO to persist message to the database
     * @param message object 
     * Response should be the message with message id
     */
    public Message postMessage(Message message) throws SQLException {
        return messageDAO.createMessage(message);
    }

    /*
     * Function: retrieve all messages from database
     * Use MessageDAO to retrieve all messages from databases
     */
    public List<Message> getAllMessages(){
        return messageDAO.getAllMessages();
    }

    /*
     * Function: retrieve all messages made by given posted_by from database
     * Use MessageDAO to retrieve all messages from user from database
     */
    public List<Message> getAllMessagesFromAccount(int posted_by) {
        return messageDAO.getAllMessagesFromAccount(posted_by);
    }

    /*
     * Function: get specific message by message_id from database
     * Use MessageDAO to retrieve specific message needed
     */
    public Message getMessageByID(int message_id) {
        return messageDAO.getMessagebyID(message_id);
    }

    /*
     * Function: delete specific message given by message_id from the database
     * Use MessageDAO to retrieve specific message needed
     */
    public Message deleteMessageByID(int message_id) {
        return messageDAO.deleteMessageByID(message_id);
    }

    /*
     * Function: update message in database given the message_id and message_text
     * Use MessageDAO to update needed message and persist change to the database
     */
    public Message updateMessage(int message_id, String message_text) {
        return messageDAO.updateMessage(message_id,message_text);
    }

}
