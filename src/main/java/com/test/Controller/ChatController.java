package com.test.Controller;


import com.test.Repository.LoginRepository;
import com.test.Service.PasswordResetService;
import com.test.Service.chatService;
import com.test.Util.JwtUtil;
import com.test.dto.FriendRequestDTO;
import com.test.model.FriendRequest;
import com.test.model.LoginRequest;
import com.test.model.User;
import com.test.model.chatMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(value = "*" , allowedHeaders = "*")
public class ChatController {

    private final chatService chatService;
    @Autowired
    private LoginRepository loginRepository;


    @Autowired
    private JwtUtil jwtUtil;

    public ChatController(chatService chatService , LoginRepository loginRepository) {
        this.chatService = chatService;
        this.loginRepository = loginRepository;

    }
    private String getRoom(String user1, String user2) {

        return user1.compareTo(user2) < 0
                ? user1 + "_" + user2
                : user2 + "_" + user1;
    }
/// ///////////////////////////////////////////////

    @MessageMapping("/sendMessage")//It is PostMapping but in WebSOcket
    public void sendMessage(chatMessage message) {

        String room = getRoom(message.getSender(), message.getReceiver());
        message.setRoom(room);
        message.setTimestamp(System.currentTimeMillis());

        if(message.getReplyTo() != null && message.getReplyTo().getId() != null ){
            chatMessage parent = chatService.findById(message.getReplyTo().getId());

            message.setReplyTo(parent);
        }

        chatService.saveMessage(message);
       chatService.sendMessageToRoom(room, message);
    }
    /// /////////////////////////////////////
    @GetMapping("/getMessages")
    public List<chatMessage> getMessages(@RequestParam("sender") String sender, @RequestParam("receiver") String receiver) {
        String room = getRoom(sender, receiver);
        return chatService.getMessagesByRoom(room);
    }
    ///  //////////////////////////////////////
    @GetMapping("/messages/{room}")
    public List<chatMessage> getMessagesByRoom(@PathVariable("room") String room) {
        return chatService.getMessagesByRoom(room);
    }
    /// ////////////////////////////////////
    @GetMapping("/all")
    public List<chatMessage> getAll() {
        return chatService.getAllMessages();
    }
   /////////////////////////////////
    @GetMapping("/allChats")
    public List<chatMessage> all() {
        return chatService.getRecentMessages();
    }
    /// ////////////////////////////
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        // find user in DB
        User user = loginRepository.findByEmail(req.getEmail())
                .orElse(null);

        if (user == null || !user.getPassword().equals(req.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // generate token
        String token = jwtUtil.generateToken(user.getEmail());

        // return token + user info
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getId());
        response.put("email", user.getEmail());

        return ResponseEntity.ok(response);
    }
    /// ///////////////////////////////////
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user){
        String result = chatService.register(user);
        if(result.equals("Email already exists")){
            return ResponseEntity.status(409).body(result);
        }

        return ResponseEntity.ok(result);
    }
/// ////////////////////////////////////////
    @GetMapping("/users")
    public List<String> getAllUsers(HttpSession session) {
        var currentUser = session.getAttribute("user");
        return chatService.getAllUsernames()
                .stream()
                .filter(u -> !u.equals(currentUser))  // exclude self
                .collect(java.util.stream.Collectors.toList());
    }
    /// ///////////////
    @DeleteMapping("/delete")
    public void  deleteUser(@RequestParam("userId") long userId) {
        chatService.deleteUser(userId);
    }
    /// ////////////////
    @DeleteMapping("/deleteChats/{id}")
    public void deleteChats(@PathVariable chatMessage id){
        chatService.deleteChats(id);
    }
    /// ///////////////////////////////////////
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String Email){
        Optional<User> user = chatService.search(Email);
        if(user.isEmpty()){
            return ResponseEntity.status(404).body("User not found");

        }
        return ResponseEntity.ok(user.get());
    }
/////////////////////////////////////////////////////////
@PostMapping("/FriendRequest")
public List<String> sendRequest(
        @RequestBody FriendRequestDTO dto,
        @RequestHeader(value = "Authorization", required = false) String authHeader) {

    System.out.println("=== HEADER VALUE: [" + authHeader + "]");
    System.out.println("=== DTO: [" + dto.getReceiverId() + "]");

    if (authHeader == null) {
        return Collections.singletonList("ResponseEntity.status(401)");
    }

    if (!authHeader.startsWith("Bearer ")) {
        return Collections.singletonList("header does not start with Bearer, actual value:");
    }

    String token = authHeader.substring(7);
    String senderEmail = jwtUtil.extractEmail(token);
    User sender = loginRepository.findByEmail(senderEmail).orElseThrow();
     chatService.request(dto.getReceiverId(), sender);
     return Collections.singletonList("request sent");
}
    /////////////////////////////////////

    @GetMapping("/Friend-Request/Pending")
    public List<FriendRequest> getFriendRequest(@RequestParam String email){
         User currentUser = loginRepository.findByEmail(email).orElseThrow();

       return chatService.getFriendRequest(currentUser);
    }
    /////////////////////////////////////////
    @PostMapping("/friend-request/accept/{id}")
    public String acceptRequest(@PathVariable Long id) {
        return chatService.Accept(id);

    }
    /// //////////////////////////////////
    @PostMapping("/friend-request/reject/{id}")
    public String rejectRequest(@PathVariable Long id) {
        return chatService.reject(id);
    }

    @GetMapping("/friends/{email}")
    public List<String> getFriends(@PathVariable String email) {
        return chatService.getFriends(email);
    }

    @PostMapping("/update-avatar")
    public ResponseEntity<String> updateAvatar(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = loginRepository.findByEmail(email).orElseThrow();
        user.setAvatarUrl(body.get("avatarUrl"));
        loginRepository.save(user);
        return ResponseEntity.ok("Avatar updated");
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = loginRepository.findByEmail(email).orElseThrow();
        Map<String, Object> res = new HashMap<>();
        res.put("email", user.getEmail());
        res.put("id", user.getId());
        res.put("avatarUrl", user.getAvatarUrl());
        return ResponseEntity.ok(res);
    }

    //reset password email
    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        return ResponseEntity.ok(passwordResetService.sendResetLink(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                passwordResetService.resetPassword(body.get("token"), body.get("password"))
        );
    }
    @DeleteMapping("/unfriend")
    public void unfriend( @RequestParam String email, @RequestParam String friendEmail){
        chatService.unfriend(email, friendEmail);
    }
    @PostMapping("/messages/{room}/seen")
    public ResponseEntity<String> markRoomSeen(@PathVariable String room , @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        chatService.markRoomAsSeen(room , email);
        return ResponseEntity.ok("seen");
    }

    @GetMapping("/messages/{room}/unread")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String room , @RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));

        return ResponseEntity.ok(chatService.getUnreadCount(room , email));
    }
}


