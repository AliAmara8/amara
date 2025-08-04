package com.ali.amara.reaction.repository;

import com.ali.amara.reaction.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByAuthor_IdAndPost_Id(Long authorId, Long postId);

    Optional<Reaction> findByAuthor_IdAndComment_Id(Long authorId, Long commentId);
}