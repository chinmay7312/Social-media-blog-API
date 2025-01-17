package Controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    
    AccountService accountService = new AccountService();
    MessageService messageService = new MessageService();
    
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerAccountHandler);
        app.post("/login", this::loginToAccountHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getMessagesHandler);
        app.get("messages/{message_id}",this::getMessageByIDHandler);
        app.delete("messages/{message_id}", this::deleteMessageByIDHandler);
        app.patch("messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesofUserID);

        return app;
    }

    /**
     * Handler to register a new account
     * The Jackson ObjectMapper will automatically convert the JSON of the POST request into an Account object.
     * If AccountService returns a null account (the account registration was unsuccessful), the API will return a 
     * 400 message (client error)
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void registerAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        // perform checks on username and password validity

        // username cannot be blank
        if (account.getUsername() == null || account.getUsername().isEmpty()) {
            ctx.status(400);
            return;
        }

        // password must be at least 4 characters long
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            ctx.status(400);
            return;
        }

        // username must not already exist in database is handled in database 
        // just need to handle exception that will be thrown in DAO class

        Account registeredAccount = accountService.registerAccount(account);
        if (registeredAccount != null) {
            ctx.json(mapper.writeValueAsString(registeredAccount));
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }

    /*
     * Handler to check login: will be successful only if username and password from 
     * request body match a real account in the database
     * Should return in the response body the JSON representation of the account including account_id
     * with return status 200 
     * If login unsuccessful the response status should be 401
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void loginToAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        Account accountLogin = accountService.loginToAccount(account);
        if(accountLogin != null) {
            ctx.json(mapper.writeValueAsString(accountLogin));
            ctx.status(200);
        } else {
            ctx.status(401);
        }
    }

    /*
     * Handler for posting messages: will be successfull if the message_text is not blank, it 
     * is not over 255 characters, and posted_by refers to a real and existing user
     * Should return in response body the JSON representation of the message including the
     * message_id with status code 200
     * If message creation was unsucessful response status should be set to 400
     */
    private void createMessageHandler(Context ctx) throws JsonProcessingException, SQLException{
        ObjectMapper mapper = new ObjectMapper();
        Message newMessage = mapper.readValue(ctx.body(), Message.class);

        // perform checks on message validity
        
        // check if message is not empty
        if(newMessage.message_text == null || newMessage.message_text.isEmpty()) {
            ctx.status(400);
            return;
        }

        // check if message is not over 255 characters
        if(newMessage.message_text.length() > 255) {
            ctx.status(400);
            return;
        }

        Message createMessage = messageService.postMessage(newMessage);
        if (createMessage != null) {
            ctx.json(mapper.writeValueAsString(createMessage));
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }

    /*
     * Handler for retrieving all messages: response body will be JSON representation of a list
     * containing all messages from databases; should return empty if there are no messages
     * Response status should always be 200
     */
    private void getMessagesHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
        ctx.status(200);
    }

    /*
     * Handler for getting message by the specified message_id: the response body will a JSON
     * representation of the message identified; should return empty if there is no such message
     * Response status should always be 200
     */ 
     private void getMessageByIDHandler(Context ctx) {
        int message_id = ctx.pathParamAsClass("message_id", Integer.class).get();
        Message message = messageService.getMessageByID(message_id);

        if (message != null) {
            ctx.json(message);
            ctx.status(200);
        } else {
            ctx.status(200);
        }
    }

    /*
     * Handler for deleting a messafe by the specified message_id: the response body will have
     * a JSON representation of the deleted message; should return empty if there is no such message
     * Response status should be 200 if the message was found or not
     */
    private void deleteMessageByIDHandler(Context ctx) {
        int message_id = ctx.pathParamAsClass("message_id", Integer.class).get();
        Message deletedMessage = messageService.deleteMessageByID(message_id);

        if (deletedMessage != null) {
            ctx.json(deletedMessage);
            ctx.status(200);
        } else {
            ctx.status(200);
        }
    }

    /*
     * Handler for updating a message in the databasse given the message_id as a path parameter 
     * and the request body contains a new message_text value to replace the message idenfitied 
     * by message_id; response body contains the full updated message including everything in JSON
     * Will be successful if the message_id already exists and the new message_text is not blank
     * and is not over 255 characters
     * If the update is successful response body should contain the full updated message and 
     * response status should be set to 200
     * If the update is not successful, response status should be 400
     */
    private void updateMessageHandler(Context ctx) throws JsonProcessingException {
        int message_id = ctx.pathParamAsClass("message_id", Integer.class).get();
        ObjectMapper mapper = new ObjectMapper();
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = mapper.readValue(ctx.body(), Map.class);
        String message_text = body.get("message_text");

        // check if message_text meets requirement

         // check if message is not empty
         if(message_text == null || message_text.isEmpty()) {
            ctx.status(400);
            return;
        }

        // check if message is not over 255 characters
        if(message_text.length() > 255) {
            ctx.status(400);
            return;
        }

        Message updatedMessage = messageService.updateMessage(message_id,message_text);
        if (updatedMessage != null) {
            ctx.json(updatedMessage);
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }

    /*
     * Handler for retrieving all messages written by a user given their account_id
     * Response body should have JSON representation of a list containing all messages posted by
     * the user from the database, should be empty if there are no messages
     * Response status should always be 200
     */
    private void getMessagesofUserID(Context ctx) {
        int posted_by = ctx.pathParamAsClass("account_id", Integer.class).get();
        List<Message> messages = messageService.getAllMessagesFromAccount(posted_by);
        ctx.json(messages);
        ctx.status(200);
    }

    





}