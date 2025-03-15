package com.medfactor.factorusers.service;

import com.medfactor.factorusers.dtos.*;
import com.medfactor.factorusers.entities.User;

import java.util.List;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);

    void createUser(UserRequest userRequest);
    void sendEmailUser(User u,String password);

    void codeVerficationCreation(String email);
    Boolean verifyCode(String email,String code);

    void changePassword(ResetRequest resetRequest);
    void changePasswordFirstTime(ResetFirstTimeRequest resetRequest);
    User getByEmail(String  email);
    User getUserLoggedInUser(String email);
    List<UserResponse> getAllUserByRole(String role);

    UserResponse updateUserRoles(List<String> roles, Long id);

    User getUserById(Long id);
    String randomCodeGenerator(int i,boolean type);
    User updateUser(User user);
    void deleteUser(Long id);
}
