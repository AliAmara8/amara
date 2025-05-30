package com.ali.amara.relationship;

import com.ali.amara.user.UserDTO;
import com.ali.amara.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    Optional<Relationship> findByUserAndFriend(UserEntity user, UserEntity friend);
    Optional<Relationship> findByFriendAndUser(UserEntity friend, UserEntity user);
    List<Relationship> findByUserAndStatus(UserEntity user, RelationshipStatus status);
    Optional<Relationship> findByUserAndFriendAndStatus(UserEntity user, UserEntity friend, RelationshipStatus status);
    Optional<Relationship> findByUserIdAndFriendIdAndStatus(Long userId, Long friendId, RelationshipStatus status);


    @Query("SELECT u FROM UserEntity u WHERE u.id <> :userId " +
            "AND NOT EXISTS (SELECT 1 FROM Relationship r WHERE (r.user.id = :userId AND r.friend.id = u.id)) " +
            "AND NOT EXISTS (SELECT 1 FROM Relationship r WHERE (r.friend.id = :userId AND r.user.id = u.id))")
    List<UserEntity> findUsersNotFriends(@Param("userId") Long userId);

    @Query("""
    SELECT r FROM Relationship r 
    WHERE (r.user.id = :userId OR r.friend.id = :userId) 
    AND r.status = 'ACCEPTED'
""")
    List<Relationship> findAllFriendshipsByUserId(@Param("userId") Long userId);
}

