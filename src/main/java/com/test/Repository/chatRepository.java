package com.test.Repository;



import com.test.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface chatRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoom(String room);
    Page<ChatMessage> findAllByOrderByTimestampDesc(Pageable pageable);



}
