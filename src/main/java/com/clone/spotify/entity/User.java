package com.clone.spotify.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles; // 사용자의 역할 목록
    @Override
    public String toString() {
        String msg = "{";
        msg += "email:"+email+", ";
        msg += "password:"+password+", ";
        msg += "birth:"+birth+", ";
        msg += "gender:"+gender;
        msg += "}";
        return msg;
    }
}

