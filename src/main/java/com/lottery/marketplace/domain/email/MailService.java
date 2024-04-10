package com.lottery.marketplace.domain.email;

import com.lottery.marketplace.domain.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
  @Autowired
  private JavaMailSender javaMailSender;

  public void sendMail(User user, String subject, String mailContent) throws MessagingException {
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    helper.setFrom("no-reply@pcdossonhos.com");
    helper.setTo(user.getEmail());
    helper.setSubject(subject);
    helper.setText(mailContent, true);
    sendMessage(mimeMessage);
  }

  public void sendMessage(MimeMessage mimeMessage) {
    this.javaMailSender.send(mimeMessage);
  }
}
