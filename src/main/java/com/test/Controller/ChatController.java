package com.test.Controller;



import com.test.model.ChatMessage;
import com.test.Service.ChatService;
import com.test.model.LoginRequest;
import com.test.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    private String getRoom(String user1, String user2) {

        return user1.compareTo(user2) < 0
                ? user1 + "_" + user2
                : user2 + "_" + user1;
    }

    @MessageMapping("/sendMessage")//It is PostMapping but in WebSOcket
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {


    public void sendMessage(chatMessage message) {

        String room = getRoom(message.getSender(), message.getReceiver());
        message.setRoom(room);
        message.setTimestamp(System.currentTimeMillis());

        chatService.saveMessage(message);

       chatService.sendMessageToRoom(room ,message);
    }
    @GetMapping("/getMessages")
    public List<chatMessage> getMessages(@RequestParam("sender") String sender, @RequestParam("receiver") String receiver) {
        String room = getRoom(sender, receiver);
        return chatService.getMessagesByRoom(room);
    }
    @GetMapping("/messages/{roomId}")
    public List<chatMessage> getMessagesByRoom(@PathVariable String roomId) {
        return chatService.getMessagesByRoom(roomId);
    }
    @GetMapping("/all")
    public List<ChatMessage> getAll() {
        return chatService.getAllMessages();
    }
    @GetMapping("/allChats")
    public List<ChatMessage> all() {
        return chatService.getRecentMessages();
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request,
                        HttpSession session){

        String response = chatService.login(request);

        if(response.equals("Login Successful")){
            session.setAttribute("user", request.getEmail());
        }

        return response;
    }
    @PostMapping("/register")
    public String register(@RequestBody User user){

        return chatService.register(user);
    }
    @GetMapping("/users")
    public List<User> getUsers() {
        return chatService.getUsers();
    }
}