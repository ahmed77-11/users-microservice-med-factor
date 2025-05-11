package com.medfactor.factorusers.controllers;


import com.medfactor.factorusers.dtos.UserRequest;
import com.medfactor.factorusers.dtos.UserResponse;
import com.medfactor.factorusers.entities.Role;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        System.out.println(new Date());
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
    public UserRequest updateUser(@RequestBody User user, Principal principal) throws Exception {
        String email = principal.getName();
        User oldUser = userService.getByEmail(email);

        if (oldUser == null) {
            throw new Exception("User not found");
        }

        user.setPassword(oldUser.getPassword());
        user.setRoles(new ArrayList<>(oldUser.getRoles())); // ✅ FIXED shared reference
        user.setForceChangePassword(oldUser.isForceChangePassword());
        user.setId(oldUser.getId());
        User newUser = userService.updateUser(user);

        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName(newUser.getFirstname());
        userRequest.setLastName(newUser.getLastname());
        userRequest.setCin(newUser.getCin());
        userRequest.setEmail(newUser.getEmail());
        userRequest.setProfilePicture(newUser.getProfilePicture());
        userRequest.setArchiver(newUser.getArchiver());

        List<String> roles = new ArrayList<>();
        for (Role role : newUser.getRoles()) {
            roles.add(role.getRole());
        }
        userRequest.setRoles(roles);

        return userRequest;
    }

    @PatchMapping("/updateMobileUser")
    public UserRequest updateUserMobile(@RequestBody User user, Principal principal) throws Exception {
        String email = principal.getName();
        User oldUser = userService.getByEmail(email);

        if (oldUser == null) {
            throw new Exception("User not found");
        }

        user.setPassword(oldUser.getPassword());
        user.setRoles(new ArrayList<>(oldUser.getRoles())); // ✅ FIXED shared reference
        user.setForceChangePassword(oldUser.isForceChangePassword());
        user.setId(oldUser.getId());
        user.setAdherent(oldUser.isAdherent());
        user.setAdherentId(oldUser.getAdherentId());
        User newUser = userService.updateUser(user);

        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName(newUser.getFirstname());
        userRequest.setLastName(newUser.getLastname());
        userRequest.setEmail(newUser.getEmail());
        userRequest.setAdherentId(newUser.getAdherentId());
        userRequest.setProfilePicture(newUser.getProfilePicture());
        userRequest.setArchiver(newUser.getArchiver());

        List<String> roles = new ArrayList<>();
        for (Role role : newUser.getRoles()) {
            roles.add(role.getRole());
        }
        userRequest.setRoles(roles);

        return userRequest;
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

    @GetMapping("/usersByRole/{role}")
    public List<UserResponse> getAllUserByRole(@PathVariable("role") String role){
        return userService.getAllUserByRole(role);
    }





}
