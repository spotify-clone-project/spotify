package com.clone.spotify.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_songs")
@IdClass(PlaylistSong.PlaylistSongId.class)
@Data
public class PlaylistSong {
    @Id
    private Long playlistId;

    @Id
    private Long songId;

    @Column(nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();

    @Data
    public static class PlaylistSongId implements Serializable {
        private Long playlistId;
        private Long songId;
    }
}

