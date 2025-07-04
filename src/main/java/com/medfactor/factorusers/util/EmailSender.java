package com.medfactor.factorusers.util;

public interface EmailSender {
    void sendEmail(String to, String email,String password);
    void sendVerifyCodeEmail(String to, String email, String code);
    void sendActionEmail(String to, String email, String action, String code);
}
