package com.test.dto;


import lombok.Getter;
import lombok.Setter;


public class FriendRequestDTO {
    @Getter @Setter
    private Long receiverId;

    @Getter @Setter
   private String senderEmail;
}
