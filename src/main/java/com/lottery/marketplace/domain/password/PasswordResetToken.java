package com.lottery.marketplace.domain.password;

import com.lottery.marketplace.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class PasswordResetToken {
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "token_id", insertable = false, updatable = false, nullable = false)
  private UUID tokenId;

  @Column(name = "expiration_time")
  private Date expirationTime;

  private static final int EXPIRATION_TIME = 10;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User userId;

  @Column(name = "is_used")
  private Boolean isUsed;

  @Column(name = "created_at")
  private Date createdAt = new Date();

  public PasswordResetToken() {
    this.expirationTime = this.getTokenExpirationTime();
  }

  public Date getTokenExpirationTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(new Date().getTime());
    calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
    return new Date(calendar.getTime().getTime());
  }
}
