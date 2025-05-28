package com.medfactor.factorusers.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
@Service
public class EmailService implements EmailSender {
    private final JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;
    @Override
    public void sendEmail(String to, String email,String password){
        try {
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message,true,"utf-8");
            Context context=new Context();
            context.setVariable("email",email);
            context.setVariable("password",password);
            context.setVariable("logoImage", "cid:logoImage");
            String emailContent=templateEngine.process("emailTemplate",context);

            helper.setTo(to);
            helper.setSubject("Votre Compte est creer dans MedFactor");
            helper.setFrom("mghirbiahmed02@gmail.com");
            helper.setText(emailContent,true);
            ClassPathResource image = new ClassPathResource("static/logoMF.jpg");
            helper.addInline("logoImage", image);

            mailSender.send(message);
        }catch (MessagingException e){
            throw new IllegalStateException("Failed to send email: " + e.getMessage(), e);
        }


    }

    @Override
    public void sendVerifyCodeEmail(String to, String email, String code) {
        try {
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message,true,"utf-8");
            Context context=new Context();
            context.setVariable("email",email);
            context.setVariable("code",code);
            context.setVariable("logoImage", "cid:logoImage");
            String emailContent=templateEngine.process("resetTemplate",context);

            helper.setTo(to);
            helper.setSubject("Votre Code de Verfication Pour change Le Mot de passe en MedFactor");
            helper.setFrom("mghirbiahmed02@gmail.com");
            helper.setText(emailContent,true);
            ClassPathResource image = new ClassPathResource("static/logoMF.jpg");
            helper.addInline("logoImage", image);

            mailSender.send(message);
        }catch (MessagingException e){
            throw new IllegalStateException("Failed to send email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendActionEmail(String to, String email, String action, String code) {
        try {
         MimeMessage message=mailSender.createMimeMessage();
         MimeMessageHelper helper=new MimeMessageHelper(message,true,"utf-8");
         Context context=new Context();
            context.setVariable("email",email);
            context.setVariable("action", action);
            context.setVariable("code", code);
            context.setVariable("logoImage", "cid:logoImage");
            String emailContent=templateEngine.process("actionTemplate",context);
            helper.setTo(to);
            helper.setSubject("Action Requise Pour Votre Compte MedFactor");
            helper.setFrom("mghirbiahmed02@gmail.com");
            helper.setText(emailContent,true);
            ClassPathResource image = new ClassPathResource("static/logoMF.jpg");
            helper.addInline("logoImage", image);

            mailSender.send(message);

        }catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
