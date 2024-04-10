package com.lottery.marketplace.domain.user;

import com.lottery.marketplace.domain.auth.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 7526472295622776147L;  // random fixed value

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", insertable = false, updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = false)
    private String identification;

    @Column(nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phone;

    @Column(name = "created_date", columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column
    private String dateOfBirthday;

    @Column
    private String addressCep;

    @Column
    private String addressStreet;

    @Column
    private String addressNumber;

    @Column
    private String addressComplement;

    @Column
    private String addressCity;

    @Column
    private String addressState;
}