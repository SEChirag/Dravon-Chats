package com.test.Repository;



import com.test.model.chatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface chatRepository extends JpaRepository<chatMessage, Long> {

    List<chatMessage> findByRoom(String room);
    Page<chatMessage> findAllByOrderByTimestampDesc(Pageable pageable);



}
