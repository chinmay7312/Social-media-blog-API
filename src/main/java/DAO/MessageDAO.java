package DAO;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Model.Message;
import Util.ConnectionUtil;

/* 
 * DAO class mediates the tranformation of data between the format of objects in Java to rows
 * in a database. 
 */

public class MessageDAO {
    
    // Helper function to check if account exists within database
    private boolean AccountExists(int account_id) throws SQLException{
        String sql = "select * from account where account_id = ?";
        try {
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, account_id);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next(); // returns true if the account exists
        } finally {

        }
    }


    /*
     * Function: create new messages in the database 
     */
    public Message createMessage(Message message) throws SQLException {
        if (AccountExists(message.getPosted_by())) {
            String sql = "insert into message (posted_by,message_text,time_posted_epoch) values (?,?,?)";
            
            try {
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                preparedStatement.setInt(1, message.getPosted_by());
                preparedStatement.setString(2, message.getMessage_text());
                preparedStatement.setLong(3, message.getTime_posted_epoch());

                preparedStatement.executeUpdate();
                ResultSet pKeyResultSet = preparedStatement.getGeneratedKeys();
                if(pKeyResultSet.next()) {
                    int generated_message_id = (int) pKeyResultSet.getLong(1);
                    return new Message(generated_message_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
                }
            } catch (SQLException e) {
                // TODO: handle exception
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    /*
     * Function: Retrieve all messages from database
     */
    public List<Message> getAllMessages() {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        
        try {
            String sql = "select * from message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                Message newMessage = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(newMessage);
            }

        } catch (SQLException e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        
        return messages;
    }

    /*
     * Function: retrieve all messages made by given user identified by posted_by
     */
    public List<Message> getAllMessagesFromAccount(int posted_by) {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();

        try {
            String sql = "select * from message where posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, posted_by);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                Message newMessage = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(newMessage);
            }

        } catch (SQLException e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }

        return messages;
    }

    /*
     * Function: Retrieve message by message_id from database
     */
    public Message getMessagebyID(int message_id) {
        try {
            Connection connection = ConnectionUtil.getConnection();
            String sql = "select * from message where message_id = ?";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                return message;
            }

        } catch (SQLException e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        return null;
    }

    /*
     * Function: delete message from database given message_id and provide deleted message
     */
    public Message deleteMessageByID(int message_id) {
        Message deletedMessage = null;
        try {
            Connection connection = ConnectionUtil.getConnection();

            Message getMessage = this.getMessagebyID(message_id);
            deletedMessage = getMessage;
            
            String deleteSql = "delete from message where message_id = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
            deleteStatement.setInt(1, message_id);

            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected == 0) {
                deletedMessage = null;
            }
            
        } catch (SQLException e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        return deletedMessage;
    }

    /*
     * Function: Update message given the message_id and the message_text
     */
    public Message updateMessage(int message_id, String message_text) {
        Message updatedMessage = null;
        try {
            String updateSql = "update message set message_text = ? where message_id = ?";
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1, message_text);
            updateStatement.setInt(2, message_id);

            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected == 0) {
                updatedMessage = null;
            } else {
                updatedMessage = this.getMessagebyID(message_id);
            }

        } catch (SQLException e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }

        return updatedMessage;
    }
}
