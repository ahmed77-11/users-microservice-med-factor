package com.medfactor.factorusers;

import com.medfactor.factorusers.dtos.VerifCodeRequest;
import com.medfactor.factorusers.entities.User;
import com.medfactor.factorusers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/actions")
public class ActionController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendEditActionEmail")
    public ResponseEntity<?> sendEditActionEmail(Principal principal) {
        String email = principal.getName();
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        User user=userService.getUserLoggedInUser(email);
        userService.sendActionVerifyCodeEmail(user.getEmail(),"edit");
        return ResponseEntity.ok("Edit action email sent successfully");
    }

    @PostMapping("/sendDeleteActionEmail")
    public ResponseEntity<?> sendDeleteActionEmail(Principal principal) {
        String email = principal.getName();
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        User user=userService.getUserLoggedInUser(email);
        userService.sendActionVerifyCodeEmail(user.getEmail(),"delete");
        return ResponseEntity.ok("Delete action email sent successfully");
    }
    @PostMapping("/verifyActionCode")
    public ResponseEntity<?> verifyActionCode(Principal principal, VerifCodeRequest verifCodeRequest) {
        String email = principal.getName();
        String code = verifCodeRequest.getCode();

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Code is required");
        }
        User user=userService.getUserLoggedInUser(email);
        boolean isVerified = userService.verifyCode(user.getEmail(), code);
        if (isVerified) {
            return ResponseEntity.ok("Action verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }
    }
}
