package com.test.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_message")
public class chatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    private String receiver;

    private String content;

    private String room;

    private Long roomId;

    private String Email;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private Long timestamp;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    private chatMessage replyTo;

@Column(nullable = false)
    private boolean seen = false;

    private Long seenAt;
}