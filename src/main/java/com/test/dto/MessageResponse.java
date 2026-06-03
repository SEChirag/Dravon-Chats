package com.test.dto;

public class MessageResponse {
    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private Long replyToId;
    private String replyToContent;
    private String replyToSender;

    private boolean seen;
    private Long seenAt;
}
