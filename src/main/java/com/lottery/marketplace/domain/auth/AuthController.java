package com.lottery.marketplace.domain.auth;

import com.lottery.marketplace.config.JwtTokenProvider;
import com.lottery.marketplace.domain.user.User;
import com.lottery.marketplace.domain.user.UserService;
import com.lottery.marketplace.util.PhoneNumberExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth (cadastro e login de usuários)")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Operation(summary = "Realiza login de usuário")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Usuário logado com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar login, senha ou e-mail incorretos."),
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateJwtToken(authentication);
        JwtResponse jwtResponse = userService.mountLoginResponse(loginRequest.getEmail(),jwt);
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(summary = "Realiza cadastro de usuário")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Usuário cadastrado com sucesso."),
      @ApiResponse(responseCode = "500", description = "Formato de telefone inválido. Utilize o formato (xx) xxxxx-xxxx.")
    })
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest userRequest) {
        PhoneNumberExtractor.isValidPhoneNumber(userRequest.getPhone());
        User userToPersist = new User();
        userToPersist.setRole(UserRole.ROLE_REGULAR);
        userToPersist.setEmail(userRequest.getEmail());
        userToPersist.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userToPersist.setIdentification(userRequest.getIdentification());
        userToPersist.setName(userRequest.getName());
        userToPersist.setLastName(userRequest.getLastName());
        userToPersist.setPhone(userRequest.getPhone());
        userToPersist.setDateOfBirthday(userRequest.getDateOfBirthday());
        userToPersist.setAddressCep(userRequest.getAddressCep());
        userToPersist.setAddressStreet(userRequest.getAddressStreet());
        userToPersist.setAddressNumber(userRequest.getAddressNumber());
        userToPersist.setAddressComplement(userRequest.getAddressComplement());
        userToPersist.setAddressCity(userRequest.getAddressCity());
        userToPersist.setAddressState(userRequest.getAddressState());



        userService.save(userToPersist);

        return ResponseEntity.ok().build();
    }
}