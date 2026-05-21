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

    public void deleteUser(long userId) {
        loginRepository.deleteById(userId);
    }

    public void deleteChats(chatMessage id) {
       chatRepository.delete(id);
    }

    public Optional<User> search(String email) {
        return loginRepository.findByEmail(email);
    }

    public String reject(long id) {
        FriendRequest request = friendRequestRepository.findById(id).orElseThrow();

        request.setStatus("REJECTED");

        friendRequestRepository.save(request);

        return "Request Rejected";
    }

public String Accept(long id){
    FriendRequest request = friendRequestRepository.findById(id).orElseThrow();

        request.setStatus("ACCEPTED");

        friendRequestRepository.save(request);

        return "request accepted";
}

    public List<FriendRequest> getFriendRequest(User currentUser) {

        return friendRequestRepository.findByReceiverAndStatus(currentUser,"PENDING");
    }


    public String request( Long receiverId ,User sender ){
    User receiver = loginRepository.findById(receiverId).orElseThrow();

    boolean alreadySent = friendRequestRepository.existsBySenderAndReceiverAndStatus(sender ,receiver,"PENDING");

        if(alreadySent){
        return "Request already sent";
    }
    FriendRequest request = new FriendRequest();

        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus("PENDING");

        friendRequestRepository.save(request);

        return "Friend Request Sent";
}
}
