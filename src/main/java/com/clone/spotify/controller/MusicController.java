package com.clone.spotify.controller;
import com.clone.spotify.entity.Album;
import com.clone.spotify.entity.Artist;
import com.clone.spotify.entity.Song;
import com.clone.spotify.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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

    @GetMapping("/albums")
    public List<Album> getAllAlbums() {
        return musicService.getAllAlbums();
    }

    @GetMapping("/songs")
    public List<Song> getAllSongs() {
        return musicService.getAllSongs();
    }
}
