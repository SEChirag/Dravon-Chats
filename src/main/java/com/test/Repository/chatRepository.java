package com.test.Repository;



import com.test.model.chatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface chatRepository extends JpaRepository<chatMessage, Long> {

    Page<chatMessage> findAllByOrderByTimestampDesc(Pageable pageable);
    List<chatMessage> findByRoomOrderByTimestampAsc(String room);

    @Query("SELECT COUNT(m) FROM chatMessage m WHERE m.room = :room AND m.seen = false AND m.receiver = :email")
    long countUnreadByRoomAndReceiver(@Param("room") String room, @Param("email") String email);

}
