package com.clone.spotify.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@IdClass(Like.LikeId.class)
@Data
public class Like {
    @Id
    private Long userId;

    @Id
    private Long songId;

    @Id
    private Long albumId;

    @Id
    private Long playlistId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Data
    public static class LikeId implements Serializable {
        private Long userId;
        private Long songId;
        private Long albumId;
        private Long playlistId;
    }
}
