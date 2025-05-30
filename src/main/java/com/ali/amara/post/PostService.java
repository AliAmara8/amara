package com.ali.amara.post;

import com.ali.amara.user.UserEntity;
import com.ali.amara.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PostDTO createPost(PostDTO postDTO, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setContent(postDTO.getContent());
        post.setImageUrl(postDTO.getImageUrl());
        post.setUser(user);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        post.setTaggedFriends(postDTO.getTaggedFriends());
        post.setActivity(postDTO.getActivity());
        post.setMood(postDTO.getMood());
        post.setDrinking(postDTO.getDrinking());
        post.setEating(postDTO.getEating());
        post.setReading(postDTO.getReading());
        post.setWatching(postDTO.getWatching());
        post.setTravel(postDTO.getTravel());
        post.setLocation(postDTO.getLocation());
        post.setLink(postDTO.getLink());
        post.setGifUrl(postDTO.getGifUrl());

        Post savedPost = postRepository.save(post);

        return convertToDTO(savedPost);
    }

    public List<PostDTO> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    public Post updatePost(Long postId, Post updatedPost) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setContent(updatedPost.getContent());
        post.setImageUrl(updatedPost.getImageUrl());
        post.setTaggedFriends(updatedPost.getTaggedFriends());
        post.setActivity(updatedPost.getActivity());
        post.setMood(updatedPost.getMood());
        post.setDrinking(updatedPost.getDrinking());
        post.setEating(updatedPost.getEating());
        post.setReading(updatedPost.getReading());
        post.setWatching(updatedPost.getWatching());
        post.setTravel(updatedPost.getTravel());
        post.setLocation(updatedPost.getLocation());
        post.setLink(updatedPost.getLink());
        post.setGifUrl(updatedPost.getGifUrl());
        post.setUpdatedAt(new Date());

        return postRepository.save(post);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    private PostDTO convertToDTO(Post post) {
        return new PostDTO(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getTaggedFriends(),
                post.getActivity(),
                post.getMood(),
                post.getDrinking(),
                post.getEating(),
                post.getReading(),
                post.getWatching(),
                post.getTravel(),
                post.getLocation(),
                post.getLink(),
                post.getGifUrl(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getLikesCount(),
                post.getCommentsCount()
        );
    }
}