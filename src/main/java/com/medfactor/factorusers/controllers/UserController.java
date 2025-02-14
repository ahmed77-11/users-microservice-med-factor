package com.medfactor.factorusers.controllers;


import com.medfactor.factorusers.dtos.UserResponse;
import com.medfactor.factorusers.entities.User;
import com.medfactor.factorusers.repos.UserRepository;
import com.medfactor.factorusers.service.UserDetailsImpl;
import com.medfactor.factorusers.service.UserService;
import jakarta.xml.ws.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/logged_in_user")
    public User getUserLoggedInUser(Principal principal){
        String email=principal.getName();
        return userService.getUserLoggedInUser(email);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable("id") Long id) {
        System.out.println(id);
        if (id == null)
            return null;
        User user=userService.getUserById(id);
        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setRoles(user.getRoles());
        userResponse.setFirstName(user.getFirstname());
        userResponse.setLastName(user.getLastname());
        userResponse.setCin(user.getCin());
        return userResponse;
    }

    @PatchMapping("/updateUser")
    public User updateUser(@RequestBody User user,Principal principal) throws Exception {
        String email = principal.getName();
        User oldUser=userService.getByEmail(email);

        if(  oldUser==null ){
            throw new Exception("User not found");
        }
        user.setPassword(oldUser.getPassword());
        user.setRoles(oldUser.getRoles());
        return userService.updateUser(user);
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteUser(Principal principal) throws Exception {

        String email = principal.getName();
        User user=userService.getByEmail(email);
        if(user==null){
            throw new Exception("User not found");
        }
        user.setArchiver(true);
        return ResponseEntity.ok("User deleted successfully");
    }




}
