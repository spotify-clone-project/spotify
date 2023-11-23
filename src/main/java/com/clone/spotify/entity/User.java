package com.clone.spotify.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Date birth;

    private String gender;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Override
    public String toString() {
        String msg = "{";
        msg += "email:"+email+", ";
        msg += "passwordHash:"+password+", ";
        msg += "dateOfBirth:"+birth+", ";
        msg += "gender:"+gender;
        msg += "}";
        return msg;
    }
}

