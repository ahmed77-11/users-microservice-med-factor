package com.medfactor.factorusers.service;

import com.medfactor.factorusers.dtos.*;
import com.medfactor.factorusers.entities.Role;
import com.medfactor.factorusers.entities.User;
import com.medfactor.factorusers.entities.VerficationToken;
import com.medfactor.factorusers.repos.RoleRepository;
import com.medfactor.factorusers.repos.UserRepository;
import com.medfactor.factorusers.repos.VerficationTokenRepository;
import com.medfactor.factorusers.security.jwt.JwtUtils;
import com.medfactor.factorusers.util.EmailSender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
         Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails=(UserDetailsImpl) authentication.getPrincipal();
        String jwt=jwtUtils.generateJwtToken(userDetails);
        if(userDetails.isForceChangePassword()){
            codeVerficationCreation(loginRequest.getEmail());
        }
        return new LoginResponse(userDetails.getId(),userDetails.getEmail(),userDetails.getCin(), userDetails.getFirstname(), userDetails.getLastname(), userDetails.getAuthorities(), jwt, userDetails.isForceChangePassword(),userDetails.getProfilePicture());
    }

    @Override
    @Transactional
    public LoginMobileResponse loginMobile(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);

        if (userDetails.isForceChangePassword()) {
            codeVerficationCreation(loginRequest.getEmail());
        }

        Map<String, Object> data = getDataFromContrat(userDetails.getAdherentId(), jwt);

        // Safely extract data with fallback values
        String contratNo = (String) data.getOrDefault("contratNo", null);
        Double limiteAuto = convertToDouble(data.get("contratLimiteFinAuto"));
        Double disponible = convertToDouble(data.get("contratFinDisponible"));
        Double utilise = convertToDouble(data.get("contratFinUtlise"));
        int contratId=(int) data. getOrDefault("id",null);

        return new LoginMobileResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                contratNo,
                userDetails.getFirstname(),
                userDetails.getLastname(),
                userDetails.getAuthorities(),
                jwt,
                userDetails.isForceChangePassword(),
                userDetails.getProfilePicture(),
                limiteAuto,
                disponible,
                utilise,
                userDetails.getAdherentId(),
                contratId
        );
    }

    private Double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return value != null ? Double.parseDouble(value.toString()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private Map<String, Object> getDataFromContrat(Long adherentId,String jwt) {
        String url="http://localhost:8083/factoring/contrat/api/find-by-adherent/"+adherentId;
        HttpHeaders headers=new HttpHeaders();
        headers.add("Cookie", "JWT_TOKEN=" +jwt);
        HttpEntity<String> entity=new HttpEntity<>(headers);
        Map response=restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        System.out.println("Response from Contrat Service: " + response);
        return response;


    }

    @Override
    public void deleteUserById(Long id) {
        User user=userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User Not Found with id: "+id));
        if(user.getArchiver()){
            throw new IllegalArgumentException("User already deleted");
        }
        user.setArchiver(true);
        userRepository.save(user);
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
    public void createUserMobile(AdherentRequest adherentRequest) {
        User user=new User();
        user.setFirstname(adherentRequest.getFirstName());
        user.setLastname(adherentRequest.getLastName());
        user.setEmail(adherentRequest.getEmail());
        user.setAdherent(true);

        String oldPassword=this.randomCodeGenerator(8,true);
        adherentRequest.setPassword(oldPassword);
        String password=passwordEncoder.encode(adherentRequest.getPassword());
        user.setPassword(password);
        user.setRoles(new ArrayList<>());
        roleRepository.findByRole("ROLE_ADHERENT").ifPresent(user.getRoles()::add);
        user.setAdherentId(adherentRequest.getAdherentId());
        this.sendEmailUser(user, adherentRequest.getPassword());
        userRepository.save(user);
    }

    @Override
    public void sendEmailUser(User u,String password) {
        emailSender.sendEmail(u.getEmail(),u.getEmail(),password);
    }

    @Override
    @Transactional
    public void codeVerficationCreation(String email) {
        User user=this.getByEmail(email);
        verificationTokenRepository.deleteAllByUser(user);
        String code=this.randomCodeGenerator(6,false);
        VerficationToken verficationToken=new VerficationToken(code,user);
        verificationTokenRepository.save(verficationToken);
        emailSender.sendVerifyCodeEmail(user.getEmail(),user.getEmail(),code);
    }

    @Override
    @Transactional
    public void sendActionVerifyCodeEmail(String email, String action) {
        User user=this.getByEmail(email);
        verificationTokenRepository.deleteAllByUser(user);
        String code = this.randomCodeGenerator(6, false);
        VerficationToken verficationToken = new VerficationToken(code, user);
        verificationTokenRepository.save(verficationToken);
        emailSender.sendActionEmail(user.getEmail(),user.getEmail(), action, code);
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
    public void changePasswordFirstTime(ResetFirstTimeRequest resetRequest) {
        if(!resetRequest.getPassword().equals(resetRequest.getConfirmPassword())){
            throw new IllegalArgumentException("Passwords do not match");
        }
        User user=this.getByEmail(resetRequest.getEmail());
        VerficationToken verficationToken=verificationTokenRepository.findByUser(user).orElseThrow(()->new IllegalArgumentException("No verification token found"));
        if(!verficationToken.getToken().equals(resetRequest.getCode())){
            throw new IllegalArgumentException("Code is incorrect");
        }
        user.setPassword(passwordEncoder.encode(resetRequest.getPassword()));
        verficationToken.setVerified(true);
        user.setForceChangePassword(false);
        userRepository.save(user);
        verificationTokenRepository.save(verficationToken);
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
    public List<UserResponse> getAllUserByRole(String role) {
        Role r=roleRepository.findByRole(role).orElseThrow(() -> new RuntimeException("Role not found"));
        List<User> users=userRepository.findUsersByRolesContaining(r);
        List<UserResponse> userResponses=new ArrayList<>();
        users.stream().map(user -> {
            UserResponse userResponse=new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setEmail(user.getEmail());
            userResponse.setRoles(user.getRoles());
            userResponse.setFirstName(user.getFirstname());
            userResponse.setLastName(user.getLastname());
            userResponse.setCin(user.getCin());
            return userResponse;
        }).forEach(userResponses::add);
        return userResponses;
    }

    @Override
    public UserResponse updateUserRoles(List<String> roles, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));

        List<Role> rolesToAdd = new ArrayList<>();

        for (String role : roles) {
            roleRepository.findByRole(role).ifPresent(existingRole -> {
                if (!user.getRoles().contains(existingRole)) { // Check if user already has the role
                    rolesToAdd.add(existingRole);
                }
            });
        }

        if (!rolesToAdd.isEmpty()) {
            user.getRoles().addAll(rolesToAdd);
            userRepository.save(user);
        }

        // Build response
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setRoles(user.getRoles());
        userResponse.setFirstName(user.getFirstname());
        userResponse.setLastName(user.getLastname());
        userResponse.setCin(user.getCin());

        return userResponse;
    }


    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("User Not Found with id: "+id));
    }

    @Override
    public User getUserMobileByAdherentId(Long id) {
        return userRepository.findByAdherentIdAndArchiver(id,false).orElseThrow(()->new UsernameNotFoundException("User Not Found with id: "+id));
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

    @Override
    public User updateUserById(UserRequest userRequest, Long id) {
        System.out.println(userRequest);
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));
        user.setFirstname(userRequest.getFirstName());
        user.setLastname(userRequest.getLastName());
        user.setCin(userRequest.getCin());
        user.setEmail(userRequest.getEmail());
        user.setArchiver(false);
        user.setForceChangePassword(false);
        List<Role> roles = new ArrayList<>();
        userRequest.getRoles().forEach(role -> roleRepository.findByRole(role).ifPresent(roles::add));
        user.setRoles(roles);
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

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllByArchiver(false);
        List<UserResponse> userResponses = new ArrayList<>();
        users.stream().map(user -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setEmail(user.getEmail());
            userResponse.setRoles(user.getRoles());
            userResponse.setFirstName(user.getFirstname());
            userResponse.setLastName(user.getLastname());
            userResponse.setCin(user.getCin());
            return userResponse;
        }).forEach(userResponses::add);
        return userResponses;
    }
}
