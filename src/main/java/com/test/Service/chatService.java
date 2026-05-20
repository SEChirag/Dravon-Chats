package com.test.Service;


import com.test.model.User;
import com.test.Repository.LoginRepository;
import com.test.model.ChatMessage;
import com.test.Repository.chatRepository;
import com.test.model.LoginRequest;
import com.test.model.chatMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final chatRepository chatRepository;
    private final LoginRepository loginRepository;
    private final SimpMessagingTemplate messagingTemplate;
    public chatService(chatRepository chatRepository , LoginRepository loginRepository , SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.loginRepository = loginRepository;
        this.messagingTemplate = messagingTemplate;
    }



public String login(LoginRequest request){
    Optional<User> user = loginRepository.findByEmail((request.getEmail()));
    if(user.isPresent()){
        User dbUser = user.get();

        if(dbUser.getPassword().equals(request.getPassword())){
            return "Login Success";
        }
    }
    return "Invalid email or Password ";
}


    public ChatMessage saveMessage(ChatMessage message) {
        return chatRepository.save(message);
    }
    public List<ChatMessage> getAllMessages() {
        return chatRepository.findAll();
    }
    public List<ChatMessage> getMessagesByRoom(String room) {
        return chatRepository.findByRoom(room);
    }

    public void deleteMessage(Long id) {
        chatRepository.deleteById(id);
    }
    public List<ChatMessage> getRecentMessages() {

        Pageable pageable = PageRequest.of(0, 30);

        return chatRepository
                .findAllByOrderByTimestampDesc(pageable)
                .getContent();
    }
    public String register(User user){

        loginRepository.save(user);

        return "User Registered";
    }

    public List<User> getUsers(){
        return loginRepository.findAll();
    }

    public void sendMessageToRoom(String room , chatMessage message) {
        messagingTemplate.convertAndSend("/topic/chat"+room , message);

    }
    public List<String> getAllUsernames() {
        return loginRepository.findAll()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }
}
