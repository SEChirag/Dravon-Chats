package com.test.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    private String content;

    private String room;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private Long timestamp;



    @Getter @Setter
    private String email;

    @Getter @Setter
    private String password;
}