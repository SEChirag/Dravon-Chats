package com.test.Repository;

import com.test.model.FriendRequest;
import com.test.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface friendRequestRepository extends JpaRepository<FriendRequest , Long> {



    Boolean existsBySenderAndReceiverAndStatus(User sender , User receiver , String status);
    List<FriendRequest> findBySenderAndStatus(User sender, String status);

    List<FriendRequest> findByReceiverAndStatus(User receiver, String status);

    @Transactional
    void deleteBySenderAndReceiver(User sender, User receiver);


}
