package com.clone.spotify.service;

import com.clone.spotify.entity.Album;
import com.clone.spotify.entity.Artist;
import com.clone.spotify.entity.Song;
import com.clone.spotify.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public Map<String, Object> getAlbum(long id) throws IllegalAccessException {
        Album album = musicRepository.getAlbum(id).orElseThrow(() -> new IllegalAccessException("잘못된 접근"));
        Set<Song> songs = musicRepository.getSongsByAlbumId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("album", album);
        result.put("songs", songs);
        return result;
    }

    public Map<String, Object> getArtist(long id) {
        Artist artist = musicRepository.getArtist(id).orElseThrow(() -> new IllegalArgumentException("잘못된 접근"));
        Set<Song> songs = musicRepository.getSongByArtistId(id);
        Map<String,Object> result = new HashMap<>();
        result.put("artist", artist);
        result.put("songs", songs);
        return result;
    }
}

