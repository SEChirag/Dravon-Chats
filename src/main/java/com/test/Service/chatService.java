package com.test.Service;


import com.test.Repository.friendRequestRepository;
import com.test.model.FriendRequest;
import com.test.model.User;
import com.test.Repository.LoginRepository;
import com.test.Repository.chatRepository;
import com.test.model.LoginRequest;
import com.test.model.chatMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class chatService {

    private final chatRepository chatRepository;
    private final LoginRepository loginRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private friendRequestRepository friendRequestRepository;

    public chatService(chatRepository chatRepository, LoginRepository loginRepository, SimpMessagingTemplate messagingTemplate, friendRequestRepository friendRequestRepository) {
        this.chatRepository = chatRepository;
        this.loginRepository = loginRepository;
        this.messagingTemplate = messagingTemplate;
        this.friendRequestRepository = friendRequestRepository;
    }


    public User login(LoginRequest request) {
        Optional<User> user = loginRepository.findByEmail((request.getEmail()));
        if (user.isPresent()) {
            User dbUser = user.get();

            if (dbUser.getPassword().equals(request.getPassword())) {
                return dbUser;
            }
        }
        return null;
    }


    public chatMessage saveMessage(chatMessage message) {
        return chatRepository.save(message);
    }

    public List<chatMessage> getAllMessages() {
        return chatRepository.findAll();
    }

    public List<chatMessage> getMessagesByRoom(String room) {
        return chatRepository.findByRoomOrderByTimestampAsc(room);
    }

    public void deleteMessage(Long id) {
        chatRepository.deleteById(id);
    }

    public List<chatMessage> getRecentMessages() {

        Pageable pageable = PageRequest.of(0, 30);

        return chatRepository
                .findAllByOrderByTimestampDesc(pageable)
                .getContent();
    }

    public String register(User user) {

        if(loginRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Username Already exists";
        }
        loginRepository.save(user);
        return "User Registered";
    }

    public List<User> getUsers() {
        return loginRepository.findAll();
    }

    public void sendMessageToRoom(String room, chatMessage message) {
        messagingTemplate.convertAndSend("/topic/chat" + room, message);

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

    public String Accept(long id) {
        FriendRequest request = friendRequestRepository.findById(id).orElseThrow();

        request.setStatus("ACCEPTED");

        friendRequestRepository.save(request);

        return "request accepted";
    }

    public List<FriendRequest> getFriendRequest(User currentUser) {

//        List<FriendRequest> friendRequests = new ArrayList<>();

        return friendRequestRepository.findByReceiverAndStatus(currentUser , "PENDING");

    }


    public String request(Long receiverId, User sender) {
        Optional<User> optionalreceiver = loginRepository.findById(receiverId);

        if (optionalreceiver.isEmpty()) {
            return "Recever not found";
        }

        User receiver = optionalreceiver.get();
        boolean alreadySent = friendRequestRepository.existsBySenderAndReceiverAndStatus(sender, receiver, "PENDING");

        if (alreadySent) {
            return "Request already sent";
        }
        FriendRequest request = new FriendRequest();

        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus("PENDING");

        friendRequestRepository.save(request);
        System.out.println(request);

        return "Friend Request Sent";
    }


    public List<String> getFriends(String email) {
        User currentUser = loginRepository.findByEmail(email).orElseThrow();
        List<String> friends = new ArrayList<>();


        List<FriendRequest> sent = friendRequestRepository.findBySenderAndStatus(currentUser, "ACCEPTED");

        for (FriendRequest r : sent) {
            friends.add(r.getReceiver().getEmail());
            }

        List<FriendRequest> accepted = friendRequestRepository.findByReceiverAndStatus(currentUser, "ACCEPTED");

        for (FriendRequest r : accepted) {
            friends.add(r.getSender().getEmail());

        }


        return friends;

    }
    @Transactional
    public void unfriend(String currentEmail, String friendEmail) {

        System.out.println("Current Email = " + currentEmail);
        System.out.println("Friend Email = " + friendEmail);

        User current = loginRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User friend = loginRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new RuntimeException("Friend user not found"));

        friendRequestRepository.deleteBySenderAndReceiver(current, friend);
        friendRequestRepository.deleteBySenderAndReceiver(friend, current);
    }

    public void markRoomAsSeen(String room , String currentUserEmail) {
        List<chatMessage> messages = chatRepository.findByRoom(room);
        long now = System.currentTimeMillis();
        messages.forEach(msg -> {
            if (!Boolean.TRUE.equals(msg.getSeen())
            && msg.getReceiver().equals(currentUserEmail)){
                msg.setSeen(true);
                msg.setSeenAt(now);
            }
        });
        chatRepository.saveAll(messages);

        messagingTemplate.convertAndSend("/topic/seen" +room , Map.of("room" ,room ,"seen",true)
        );
    }

    }

