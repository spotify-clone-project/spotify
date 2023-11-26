package com.clone.spotify.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "albums", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "artist_id"})
})@Getter
@Setter
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id", referencedColumnName = "id")
    private Artist artist;

    private Date releaseDate;
    private String coverImagePath;
}

