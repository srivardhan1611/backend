package com.hostelfinder.config;

import com.hostelfinder.entity.College;
import com.hostelfinder.entity.Role;
import com.hostelfinder.entity.User;
import com.hostelfinder.repository.CollegeRepository;
import com.hostelfinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Seeds a default admin and a sample college on first run. CHANGE THE ADMIN PASSWORD. */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final CollegeRepository colleges;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (!users.existsByEmail("admin@hostelfinder.com")) {
            User a = new User();
            a.setName("Admin");
            a.setEmail("admin@hostelfinder.com");
            a.setPasswordHash(encoder.encode("Admin@123"));
            a.setRole(Role.ADMIN);
            users.save(a);
        }
        if (colleges.count() == 0) {
            College c = new College();
            c.setCollegeName("Sample Engineering College");
            c.setAddress("Hyderabad, Telangana");
            c.setLatitude(17.4401);
            c.setLongitude(78.3489);
            colleges.save(c);
        }
    }
}
