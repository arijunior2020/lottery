package com.lottery.marketplace.domain.password;

import com.lottery.marketplace.domain.user.User;
import com.lottery.marketplace.domain.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
@Tag(name = "Passwords (senhas)")
public class PasswordController {

  private final PasswordService passwordService;
  private final UserService userService;

  @PostMapping("/send-token")
  public String sendTokenToEmail(@RequestBody SendTokenRequest sendTokenRequest, final HttpServletRequest servletRequest) throws
    MessagingException, UnsupportedEncodingException {
    return passwordService.sendTokenToEmail(sendTokenRequest.getEmail(), servletRequest);
  }

  @PostMapping("/password-reset-request")
  public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest,
                                         @RequestParam("token") String token) {
    Optional<User> user = passwordService.findUserByPasswordToken(token);
    return ResponseEntity.ok(passwordService.resetPassword(passwordResetRequest, token, user.get()));
  }
}
