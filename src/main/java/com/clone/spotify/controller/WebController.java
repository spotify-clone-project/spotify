package com.clone.spotify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/signup")
    public String join() {
        return "signup";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
