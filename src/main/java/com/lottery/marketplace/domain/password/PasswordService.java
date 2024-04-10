package com.lottery.marketplace.domain.password;

import com.lottery.marketplace.domain.email.MailService;
import com.lottery.marketplace.domain.user.User;
import com.lottery.marketplace.domain.user.UserRepository;
import com.lottery.marketplace.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordService {

  @Autowired
  private UserService userService;

  private final PasswordResetTokenRepository passwordResetTokenRepository;

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final MailService mailService;

  @Value("${url.frontend.host}")
  private String frontEndHost;

  public Object resetPassword(PasswordResetRequest passwordResetRequest, String token, User user) {
    String tokenVerification = validatePasswordResetToken(token, user);
    if (!tokenVerification.equalsIgnoreCase("valido")) {
      return ResponseEntity.internalServerError().body(validatePasswordResetToken(token, user));
    }
    changePassword(user, passwordResetRequest.getNewPassword());
    setTokenToUsed(token, user);
    return ResponseEntity.ok().build();
  }

  private void setTokenToUsed(String token, User user) {
    Optional<PasswordResetToken> token1 = passwordResetTokenRepository
      .findByTokenIdAndAndUserId(getById(token).get().getTokenId(), user.getId());
    if (token1.isPresent()) {
      token1.get().setIsUsed(true);
      passwordResetTokenRepository.save(token1.get());
    }
  }


  public String sendTokenToEmail(String email, final HttpServletRequest servletRequest) throws jakarta.mail.MessagingException, UnsupportedEncodingException {
    Optional<User> user = userService.findByUserEmailOpt(email);
    String passwordResetUrl = "";
    if (user.isPresent()) {
      String passwordResetToken = createPasswordResetTokenForUser(user);
      passwordResetUrl = passwordResetEmailLink(user.get(), applicationUrl(servletRequest), passwordResetToken);
    }
    return passwordResetUrl;
  }

  private String createPasswordResetTokenForUser(Optional<User> user) {
    PasswordResetToken passwordResetToken = new PasswordResetToken();
    passwordResetToken.setUserId(user.get());
    passwordResetToken.setIsUsed(false);
    PasswordResetToken passwordResetToken1 = passwordResetTokenRepository.save(passwordResetToken);
    return passwordResetToken1.getTokenId().toString();
  }

  private String passwordResetEmailLink(User user, String applicationUrl,
                                        String passwordToken) throws jakarta.mail.MessagingException, UnsupportedEncodingException {

    String url = applicationUrl + "/redefine/reset-password?token=" + passwordToken;
    sendPasswordResetVerificationEmail(url, user);
    return url;
  }

  private void sendPasswordResetVerificationEmail(String url, User user) throws jakarta.mail.MessagingException, UnsupportedEncodingException {
    String subject = "Recuperação de Senha";
    String mailContent =
      "<h3> Olá, " + user.getName() + ", </h3>" +
        "</br><p> Acesse o link abaixo para recuperar sua senha:</p>" + "" +
        "</br><a href=\"" + url + "\">Clique aqui para redefinir sua senha.</a>" +
        "</br><p>PC dos Sonhos.</p>";
    mailService.sendMail(user, subject, mailContent);
  }

  public String applicationUrl(HttpServletRequest request) {
    String url =  frontEndHost + request.getContextPath();
    return url;
  }

  public String validatePasswordResetToken(String token, User user) {
    Optional<PasswordResetToken> passwordResetToken = getById(token);
    if (!validateIfPasswordResetTokenExists(passwordResetToken)) {
      return "Token inexistente.";
    }
    if (!validateIfPasswordResetTokenIsExpired(passwordResetToken)) {
      return "Link expirado.";
    }
    if (!validateIfPasswordResetTokenWasAlreadyUsedByUser(passwordResetToken, user)) {
      return "Token já foi utilizado.";
    }
    return "valido";
  }

  private Optional<PasswordResetToken> getById(String token) {
    return passwordResetTokenRepository.findById(UUID.fromString(token));
  }

  private Boolean validateIfPasswordResetTokenWasAlreadyUsedByUser(Optional<PasswordResetToken> passwordResetToken, User user) {
    Optional<PasswordResetToken> token = passwordResetTokenRepository
      .findByTokenIdAndAndUserId(passwordResetToken.get().getTokenId(), user.getId());
    if (token.isPresent() && token.get().getIsUsed() == true) {
      return false;
    }
    return true;
  }

  private Boolean validateIfPasswordResetTokenExists(Optional<PasswordResetToken> passwordResetToken) {
    if (passwordResetToken.isEmpty()) {
      return false;
    }
    return true;
  }

  private Boolean validateIfPasswordResetTokenIsExpired(Optional<PasswordResetToken> passwordResetToken) {
    if (passwordResetToken.isPresent()) {
      PasswordResetToken token = passwordResetToken.get();
      Date expiration = token.getExpirationTime();
      Date current = new Date();
      if(expiration.before(current)){
        return false;
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  public Optional<User> findUserByPasswordToken(String token) {
    return Optional.ofNullable(getById(token).get().getUserId());
  }

  public void changePassword(User user, String newPassword) {
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }
}
