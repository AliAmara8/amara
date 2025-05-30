package com.ali.amara.user;

import com.ali.amara.relationship.Relationship;
import com.ali.amara.relationship.RelationshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

   // Optional<UserEntity> findByEmailOrPhone(String email, String phone);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email OR u.phone = :phone")
    Optional<UserEntity> findByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);

}
