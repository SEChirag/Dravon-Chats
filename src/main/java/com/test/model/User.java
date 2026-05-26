package com.test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
  @Getter  @Setter
    private String email;

    @Getter  @Setter

    private String password;

    @ElementCollection
    @Getter @Setter
    private List<String> friends = new ArrayList<>();

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

   private String link;

}