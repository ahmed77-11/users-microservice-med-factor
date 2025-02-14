package com.medfactor.factorusers;

import com.medfactor.factorusers.entities.Role;
import com.medfactor.factorusers.entities.User;
import com.medfactor.factorusers.repos.RoleRepository;
import com.medfactor.factorusers.repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@SpringBootApplication
public class FactorUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(FactorUsersApplication.class, args);
    }


    // 🔹 Automatically Insert an Admin User on Startup
//    @Bean
//    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) { // Check if admin exists
//                User admin = new User();
//                admin.setFirstname("admin1");
//                admin.setLastname("admin1");
//                admin.setCin("12345678");
//                admin.setEmail("admin2@gmail.com");
//                admin.setPassword(passwordEncoder.encode("admin123")); // Hash password
//                Optional<Role> userRole = roleRepository.findByRole("ROLE_ADMIN");
//
//                if (userRole.isPresent()) {
//                    admin.setRoles(Collections.singletonList(userRole.get())); // Set role
//                    userRepository.save(admin);
//                    System.out.println("✅ Admin user created! (Email: admin@gmail.com, Password: admin123, Role: USER_ROLE)");
//                } else {
//                    System.out.println("⚠️ USER_ROLE not found! Make sure it's added to the database.");
//                } // Set role
//                userRepository.save(admin);
//                System.out.println("✅ Admin user created! (Username: admin, Password: admin123)");
//            } else {
//                System.out.println("✅ Admin user already exists.");
//            }
//        };
//    }
}
