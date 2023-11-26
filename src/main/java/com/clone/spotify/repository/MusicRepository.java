package com.clone.spotify.repository;

import com.clone.spotify.entity.Album;
import com.clone.spotify.entity.Artist;
import com.clone.spotify.entity.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MusicRepository {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    @Autowired
    public MusicRepository(ArtistRepository artistRepository, AlbumRepository albumRepository, SongRepository songRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
    }

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }
}

