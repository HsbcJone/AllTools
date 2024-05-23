package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Properties;

public class EmailSender {

    public static void sendEmail(String to, String from, String subject, String text) {
        // SMTP 服务器信息配置
        String host = "xxxx"; // SMTP 服务器地址
        final String username = "xxxx"; // 登录用户名
        final String password = "xxxx"; // 登录密码

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "xx"); // 或其他端口
        props.put("mail.smtp.timeout", "30000");

        //props.put("mail.smtp.starttls.enable", "true"); // 启用 TLS

        // 创建会话
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 创建邮件消息
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            // 发送邮件
            Transport.send(message);
            LocalDateTime now = LocalDateTime.now();
            System.out.println(now.toString());

            System.out.println(now.toString() + "Mail sent successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // 邮件接收者，发送者，主题和正文
        String to = "xxxxxxx";
        String from = "xxxxxxxx";
        String subject = "Test Email from Java App";
        String text = "Hello, this is a test email " +
                "sent from a Java application!" +
                "Lets go ~";


        sendEmail(to, from, subject, text);
    }
}



