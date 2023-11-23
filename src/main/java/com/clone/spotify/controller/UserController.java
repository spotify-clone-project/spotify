package com.clone.spotify.controller;

import com.clone.spotify.entity.User;
import com.clone.spotify.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/signup")
    public ResponseEntity<User> createUser(User user) {
        // 사용자에게 받은 정보로 유저를 생성해서
        User newUser = userService.createUser(user);
        // 생성한 유저를 response에 담아 반환해준다 (로그인을 바로 시키기 위함)
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loadUser(User user) {
        // 사용자에게 받은 정보로 유저를 생성해서
        User newUser = userService.createUser(user);
        // 생성한 유저를 response에 담아 반환해준다 (로그인을 바로 시키기 위함)
        return ResponseEntity.ok(newUser);
    }


}
