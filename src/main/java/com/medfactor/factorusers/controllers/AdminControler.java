package com.medfactor.factorusers.controllers;

import com.medfactor.factorusers.dtos.UserRequest;
import com.medfactor.factorusers.dtos.UserResponse;
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


    @PostMapping("/create_user")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest){
        userService.createUser(userRequest);
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
