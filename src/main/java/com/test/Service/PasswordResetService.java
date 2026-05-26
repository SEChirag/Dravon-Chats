package com.test.Service;

import com.test.Repository.LoginRepository;
import com.test.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
public class PasswordResetService {

    @Autowired

    private LoginRepository loginRepository;
    @Autowired private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spp.base-url}")
    private String baseurl;

    public String sendResetLink(String email) {
        User user = loginRepository.findByEmail(email).orElse(null);
        if (user == null)
            return "if that mail exists , a link has been sent";

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        loginRepository.save(user);
        String resetLink = baseurl + "/reset-password.html?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("DRAVON ✦ Password Reset");
        msg.setText(
                "Click the link below to reset your password:\n\n"
                        + resetLink
                        + "\n\nThis link expires in 30 minutes."
        );
        try {
            mailSender.send(msg);
            System.out.println("MAIL SENT SUCCESSFULLY");
        }catch (Exception e) {
            e.printStackTrace();
        }

        return "If that email exists, a reset link has been sent.";
    }
        public  String resetPassword(String token , String newPassword){
        User user = loginRepository.findByResetToken(token).orElse(null);

        if(user== null)
            return "Invalid or expired token";

        if(user.getResetTokenExpiry().isBefore(LocalDateTime.now()))
            return "Token has expired";

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        loginRepository.save(user);

        return "Password reset successfully";
    }
    }

