package com.medfactor.factorusers.controllers;

import com.medfactor.factorusers.dtos.AdherentRequest;
import com.medfactor.factorusers.dtos.UserRequest;
import com.medfactor.factorusers.dtos.UserResponse;
import com.medfactor.factorusers.entities.User;
import com.medfactor.factorusers.service.UserService;
import jakarta.xml.ws.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminControler {

    @Autowired
    private UserService userService;


    @GetMapping("/all-users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/update-user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody UserRequest userRequest) {




        return ResponseEntity.ok(userService.updateUserById(userRequest,id));
    }
    @PostMapping("/create_user")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest){
        userService.createUser(userRequest);
        return ResponseEntity.ok("Creation Avec Success");
    }
    @PostMapping("/create_user_mobile")
    public ResponseEntity<?> createUserMobile(@RequestBody AdherentRequest userRequest){
        userService.createUserMobile(userRequest);
        return ResponseEntity.ok("Creation Avec Success");
    }

    @PostMapping("/addRoles/{id}")
    public UserResponse addRole(@PathVariable("id") Long id, @RequestBody Map<String, List<String>> rolesMap) {
        List<String> roles = rolesMap.get("roles"); // Extract roles list from the object
        System.out.println("Received roles: " + roles);
        return userService.updateUserRoles(roles, id);
    }

    @DeleteMapping("/deleteUserById/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User Deleted");
    }



}
