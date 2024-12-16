package com.capstone3.GroupAppoint.kakao.auth.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.capstone3.GroupAppoint.kakao.auth.repository.UserRepository;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;

@Component
public class DummyDataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository.save(new User("강재일", "hong@example.com"));
            userRepository.save(new User("김희환", "kim@example.com"));
            userRepository.save(new User("염한울", "lee@example.com"));
        }

        userRepository.findAll().forEach(user -> {
            System.out.println("User: " + user.getName() + ", Email: " + user.getEmail());
        });
    }
}