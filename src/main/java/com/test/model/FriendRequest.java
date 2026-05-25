package com.test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "friend_request")
public class FriendRequest {

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
@Getter @Setter
    private Long id;

@ManyToOne
@JoinColumn(name = "sender_id")
@Getter @Setter
    private User sender;

@ManyToOne
@JoinColumn(name = "receiver_id")

@Getter @Setter
    private User receiver;
@Getter @Setter
    private String status;

}
