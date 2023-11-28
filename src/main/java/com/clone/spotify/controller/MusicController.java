package com.clone.spotify.controller;
import com.clone.spotify.entity.Album;
import com.clone.spotify.entity.Artist;
import com.clone.spotify.entity.Song;
import com.clone.spotify.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/music")
@Slf4j
public class MusicController {

    private final MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/artists")
    public List<Artist> getAllArtists() {
        return musicService.getAllArtists();
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<?> getArtists(@PathVariable long id) throws IllegalAccessException {
        Map<String, Object> result = musicService.getArtist(id);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/albums")
    public List<Album> getAllAlbums() {
        return musicService.getAllAlbums();
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<?> getAlbum(@PathVariable long id) throws IllegalAccessException {
        Map<String, Object> result = musicService.getAlbum(id);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/songs")
    public List<Song> getAllSongs() {
        return musicService.getAllSongs();
    }
}
