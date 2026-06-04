package com.test.test.Service.ChatService;
import com.test.Service.chatService;
import com.test.Repository.LoginRepository;
import com.test.Repository.chatRepository;
import com.test.model.LoginRequest;
import com.test.model.User;
import com.test.model.chatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class test {

    @Mock
    private chatRepository chatRepository;

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private chatService chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("1234");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("1234");

        when(loginRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        User result = chatService.login(request);

        assertNotNull(result);

        assertEquals("test@gmail.com", result.getEmail());
    }

    @Test
    void testLoginFailure() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("wrong");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("1234");

        when(loginRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        User result = chatService.login(request);

        assertNull(result);
    }

    @Test
    void testSaveMessage() {

        chatMessage message = new chatMessage();
        message.setEmail("Hello");

        when(chatRepository.save(message)).thenReturn(message);

        chatMessage saved = chatService.saveMessage(message);

        assertEquals("Hello", saved.getEmail());
    }

    @Test
    void testGetAllMessages() {

        chatMessage m1 = new chatMessage();
        chatMessage m2 = new chatMessage();

        when(chatRepository.findAll())
                .thenReturn(Arrays.asList(m1, m2));

        List<chatMessage> messages = chatService.getAllMessages();

        assertEquals(2, messages.size());
    }

    @Test
    void testGetMessagesByRoom() {

        chatMessage m1 = new chatMessage();
        m1.setRoom("general");

        when(chatRepository.findByRoomOrderByTimestampAsc("general"))
                .thenReturn(List.of(m1));

        List<chatMessage> messages =
                chatService.getMessagesByRoom("general");

        assertEquals(1, messages.size());
        assertEquals("general", messages.get(0).getRoom());
    }

    @Test
    void testDeleteMessage() {

        Long id = 1L;

        doNothing().when(chatRepository).deleteById(id);

        chatService.deleteMessage(id);

        verify(chatRepository, times(1)).deleteById(id);
    }

    @Test
    void testGetRecentMessages() {

        chatMessage m1 = new chatMessage();
        chatMessage m2 = new chatMessage();

        Page<chatMessage> page =
                new PageImpl<>(Arrays.asList(m1, m2));

        when(chatRepository.findAllByOrderByTimestampDesc(any(Pageable.class)))
                .thenReturn(page);

        List<chatMessage> messages =
                chatService.getRecentMessages();

        assertEquals(2, messages.size());
    }

    @Test
    void testRegister() {

        User user = new User();
        user.setEmail("new@gmail.com");

        when(loginRepository.save(user)).thenReturn(user);

        String result = chatService.register(user);

        assertEquals("User Registered", result);

        verify(loginRepository, times(1)).save(user);
    }
}