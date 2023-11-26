package com.clone.spotify.service;

import com.clone.spotify.entity.Album;
import com.clone.spotify.entity.Artist;
import com.clone.spotify.entity.Song;
import com.clone.spotify.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MusicService {

    private final MusicRepository musicRepository;

    @Autowired
    public MusicService(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    public List<Artist> getAllArtists() {
        return musicRepository.getAllArtists();
    }

    public List<Album> getAllAlbums() {
        return musicRepository.getAllAlbums();
    }

    public List<Song> getAllSongs() {
        return musicRepository.getAllSongs();
    }
}

