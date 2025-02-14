package com.medfactor.factorusers.service;

import com.medfactor.factorusers.dtos.LoginRequest;
import com.medfactor.factorusers.dtos.LoginResponse;
import com.medfactor.factorusers.dtos.ResetRequest;
import com.medfactor.factorusers.dtos.UserRequest;
import com.medfactor.factorusers.entities.User;
import com.medfactor.factorusers.entities.VerficationToken;
import com.medfactor.factorusers.repos.RoleRepository;
import com.medfactor.factorusers.repos.UserRepository;
import com.medfactor.factorusers.repos.VerficationTokenRepository;
import com.medfactor.factorusers.security.jwt.JwtUtils;
import com.medfactor.factorusers.util.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private VerficationTokenRepository verificationTokenRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
         Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails=(UserDetailsImpl) authentication.getPrincipal();
        String jwt=jwtUtils.generateJwtToken(userDetails);

        return new LoginResponse(userDetails.getEmail(),userDetails.getCin(), userDetails.getFirstname(), userDetails.getLastname(), userDetails.getAuthorities(), jwt);
    }

    @Override
    public void createUser(UserRequest userRequest) {
        User user=new User();
        user.setFirstname(userRequest.getFirstName());
        user.setLastname(userRequest.getLastName());
        user.setCin(userRequest.getCin());
        user.setEmail(userRequest.getEmail());
        String oldPassword=this.randomCodeGenerator(8,true);
        userRequest.setPassword(oldPassword);
        String password=passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(password);
        user.setRoles(new ArrayList<>());
        userRequest.getRoles().forEach(role ->
                roleRepository.findByRole(role).ifPresent(user.getRoles()::add)
        );

        this.sendEmailUser(user, userRequest.getPassword());
    userRepository.save(user);



    }

    @Override
    public void sendEmailUser(User u,String password) {
        emailSender.sendEmail(u.getEmail(),u.getEmail(),password);
    }

    @Override
    public void codeVerficationCreation(String email) {
        User user=this.getByEmail(email);
        verificationTokenRepository.deleteAllByUser(user);
        String code=this.randomCodeGenerator(6,false);
        VerficationToken verficationToken=new VerficationToken(code,user);
        verificationTokenRepository.save(verficationToken);
        emailSender.sendVerifyCodeEmail(user.getEmail(),user.getEmail(),code);

    }

    @Override
    public Boolean verifyCode(String email, String code) {
        VerficationToken verficationToken = verificationTokenRepository.findByToken(code).orElseThrow(() -> new UsernameNotFoundException("Token Not Found"));

        User user = verficationToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if (verficationToken.getToken().equals(verficationToken) && ((verficationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0)) {
            return false;
        }
    verficationToken.setVerified(true);
        verificationTokenRepository.save(verficationToken);
        return true;
    }

    @Override
    public void changePassword(ResetRequest resetRequest) {
        if(!resetRequest.getPassword().equals(resetRequest.getConfirmPassword())){
            throw new IllegalArgumentException("Passwords do not match");
        }
        User user=this.getByEmail(resetRequest.getEmail());
        VerficationToken verficationToken=verificationTokenRepository.findByUser(user).orElseThrow(()->new IllegalArgumentException("No verification token found"));
        if(!verficationToken.isVerified()){
            throw new IllegalArgumentException("No verification token found");
        }
        user.setPassword(passwordEncoder.encode(resetRequest.getPassword()));
        userRepository.save(user);

    }


    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User Not Found with email: "+email));
    }

    @Override
    public User getUserLoggedInUser(String email) {
        return this.getByEmail(email);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User Not Found with id: "+id));
    }

    @Override
    public String randomCodeGenerator(int i,boolean type) {
       final String CHARACTERS=type?"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789":"0123456789";
       final SecureRandom RANDOM=new SecureRandom();
         StringBuilder sb=new StringBuilder(i);
            for (int j = 0; j < i; j++) {
                sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
            }

        return sb.toString();
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user=userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User Not Found with id: "+id));
        if(user.getArchiver()){
            throw new IllegalArgumentException("User already deleted");
        }
        user.setArchiver(true);
        userRepository.save(user);
    }
}
