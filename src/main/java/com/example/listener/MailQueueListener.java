package com.example.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    public void sendMailMessage(Map<String, Object> data){
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type){
            case "register" -> createMessage("欢迎注册",
                    "您的邮件注册验证码为："+code+"，有效时间为3分钟", email);
            case "reset" -> createMessage("您的密码重置邮件",
                    "你好，您正在进行重置密码操作，验证码为："+code+"，有效时间3分钟", email);
            default -> null;
        };
        if(message == null) return;
        sender.send(message);

    }

    private SimpleMailMessage createMessage(String title, String content, String target){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(target);
        message.setFrom(username);
        return message;
    }
}
