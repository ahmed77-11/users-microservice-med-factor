package com.medfactor.factorusers.service;

import com.medfactor.factorusers.dtos.LoginRequest;
import com.medfactor.factorusers.dtos.LoginResponse;
import com.medfactor.factorusers.dtos.ResetRequest;
import com.medfactor.factorusers.dtos.UserRequest;
import com.medfactor.factorusers.entities.User;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);

    void createUser(UserRequest userRequest);
    void sendEmailUser(User u,String password);

    void codeVerficationCreation(String email);
    Boolean verifyCode(String email,String code);

    void changePassword(ResetRequest resetRequest);
    User getByEmail(String  email);
    User getUserLoggedInUser(String email);
    User getUserById(Long id);
    String randomCodeGenerator(int i,boolean type);
    User updateUser(User user);
    void deleteUser(Long id);
}
