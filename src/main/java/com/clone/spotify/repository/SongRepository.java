package com.clone.spotify.repository;

import com.clone.spotify.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    Set<Song> findByAlbumId(long id);
}
