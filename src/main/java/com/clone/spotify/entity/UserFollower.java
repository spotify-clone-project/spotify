package com.clone.spotify.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_followers")
@IdClass(UserFollower.UserFollowerId.class)
@Data
public class UserFollower {
    @Id
    private Long followerId;

    @Id
    private Long followedId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Data
    public static class UserFollowerId implements Serializable {
        private Long followerId;
        private Long followedId;
    }
}
