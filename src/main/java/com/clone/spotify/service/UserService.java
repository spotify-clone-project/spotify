package com.clone.spotify.service;

import com.clone.spotify.entity.Role;
import com.clone.spotify.entity.User;
import com.clone.spotify.repository.RoleRepository;
import com.clone.spotify.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User createUser(User user) {
        log.info("유저를 생성하는중... : " + user);

        // 평문 패스워드를 불러옴
        String pass = user.getPassword();

        // 암호화 시킴
        String hashedPass = passwordEncoder.encode(pass);

        // 암호화된 패스워드를 저장함
        user.setPassword(hashedPass);

        // 'ROLE_USER' 권한을 찾거나 생성.
        Role defaultRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            return roleRepository.save(newRole);
        });

        user.setRole(defaultRole);

        return userRepository.save(user);
    }
}

