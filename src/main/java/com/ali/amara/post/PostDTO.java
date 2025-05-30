package com.ali.amara.post;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

public class PostDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private Long userId; // Juste l’ID de l’utilisateur
    private String userName; // Nom complet pour affichage
    @JsonIgnore
    private List<String> taggedFriends; // Liste des amis taggés (optionnel)
    private String activity;
    private String mood;
    private String drinking;
    private String eating;
    private String reading;
    private String watching;
    private String travel;
    private String location;
    private String link;
    private String gifUrl;
    private Date createdAt;
    private Date updatedAt;
    private int likesCount;
    private int commentsCount;

    // Constructeur

    public PostDTO() { }

    public PostDTO(Long id, String content, String imageUrl, Long userId, String userName, List<String> taggedFriends,
                   String activity, String mood, String drinking, String eating, String reading, String watching,
                   String travel, String location, String link, String gifUrl, Date createdAt, Date updatedAt,
                   int likesCount, int commentsCount) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userName = userName;
        this.taggedFriends = taggedFriends;
        this.activity = activity;
        this.mood = mood;
        this.drinking = drinking;
        this.eating = eating;
        this.reading = reading;
        this.watching = watching;
        this.travel = travel;
        this.location = location;
        this.link = link;
        this.gifUrl = gifUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public List<String> getTaggedFriends() { return taggedFriends; }
    public void setTaggedFriends(List<String> taggedFriends) { this.taggedFriends = taggedFriends; }
    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getDrinking() { return drinking; }
    public void setDrinking(String drinking) { this.drinking = drinking; }
    public String getEating() { return eating; }
    public void setEating(String eating) { this.eating = eating; }
    public String getReading() { return reading; }
    public void setReading(String reading) { this.reading = reading; }
    public String getWatching() { return watching; }
    public void setWatching(String watching) { this.watching = watching; }
    public String getTravel() { return travel; }
    public void setTravel(String travel) { this.travel = travel; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getGifUrl() { return gifUrl; }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
}