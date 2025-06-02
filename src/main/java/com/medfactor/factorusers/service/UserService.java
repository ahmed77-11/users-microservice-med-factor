package com.medfactor.factorusers.service;

import com.medfactor.factorusers.dtos.*;
import com.medfactor.factorusers.entities.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);
    LoginMobileResponse loginMobile(LoginRequest loginRequest);



    void deleteUserById(Long id);




    void createUser(UserRequest userRequest);

    void createUserMobile(AdherentRequest adherentRequest);
    void sendEmailUser(User u,String password);

    void codeVerficationCreation(String email);
    void sendActionVerifyCodeEmail(String email,String action);
    Boolean verifyCode(String email,String code);

    void changePassword(ResetRequest resetRequest);
    void changePasswordFirstTime(ResetFirstTimeRequest resetRequest);
    User getByEmail(String  email);
    User getUserLoggedInUser(String email);
    List<UserResponse> getAllUserByRole(String role);

    UserResponse updateUserRoles(List<String> roles, Long id);

    User getUserById(Long id);
    User getUserMobileByAdherentId(Long id);
    String randomCodeGenerator(int i,boolean type);
    User updateUser(User user);
    User updateUserById(UserRequest userRequest, Long id);
    void deleteUser(Long id);

    List<UserResponse> getAllUsers();

    Long getUserCount();
}
