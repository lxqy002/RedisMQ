package com.dbgo.acme.redis.adapter;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

import javax.annotation.PostConstruct;

/**
 * 邮件适配器
 *
 * @author lixiao
 * @date 2018年4月20日
 * @version V1.0
 */
@Service
public class MailAdapter {

    private JavaMailSenderImpl mailSender;

    /**
     * 发送邮件
     *
     * @param to
     */
    public void sendMail(String to) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("123@qq.com");
        mailMessage.setTo(to);
        mailMessage.setSubject("消息队列处理成功");
        mailMessage.setText("这是一个测试邮件");
        mailSender.send(mailMessage);
    }

    /**
     * 配置邮件发送器
     *
     * @throws Exception
     */
    @PostConstruct
    public void initMailSender() throws Exception {
        mailSender = new JavaMailSenderImpl();
        // 用户名
        mailSender.setUsername("1234@qq.com");
        // SMTP客户端的授权码
        mailSender.setPassword("********");
        // 发件人邮箱的 SMTP 服务器地址
        mailSender.setHost("smtp.qq.com");
        // 邮件服务器监听的端口
        mailSender.setPort(465);
        // 协议SMTP+SSL
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
