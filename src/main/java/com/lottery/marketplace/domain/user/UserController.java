package com.lottery.marketplace.domain.user;

import com.lottery.marketplace.domain.ticket.TicketResponse;
import com.lottery.marketplace.domain.ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "Users (usuários)")
class UserController {

    private final TicketService ticketService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Realiza busca de dados de usuário com base em e-mail cadastrado.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar busca por e-mail.")
    })
    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> findByEmail(Authentication authentication, @PathVariable final String email){

        User userPersisted = userService.findByEmail(email);

        UserResponse userResponse = UserResponse.builder()
                .identification(userPersisted.getIdentification())
                .name(userPersisted.getName())
                .email(userPersisted.getEmail())
                .build();

        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Realiza busca de tickets de usuário com base em e-mail cadastrado.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar busca de tickets por e-mail.")
    })
    @GetMapping("/{email}/tickets")
    public ResponseEntity<Page<TicketResponse>> findTicketsByEmail(Authentication authentication,
                                                                   @PathVariable final String email,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "createdDate") String sortBy,
                                                                   @RequestParam(defaultValue = "desc") String direction){
        validateEmail(email, authentication);

        Sort sort = "asc".equalsIgnoreCase(direction) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        return ResponseEntity.ok(ticketService.findTicketByEmail(email, PageRequest.of(page, size, sort)));
    }

    @Operation(summary = "Realiza atualização de dados de usuário com base em e-mail.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Alteração realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar alteração de dados do usuário.")
    })
    @PutMapping("/{email}")
    public ResponseEntity<Void> updateUserInformation(Authentication authentication,
                                                      @PathVariable final String email,
                                                      @RequestBody @Valid UpdateUserInformationRequest userToUpdate){
        validateEmail(email, authentication);

        User userPersisted = userService.findByEmail(email);

        userPersisted.setName(userToUpdate.getName());
        userPersisted.setLastName(userToUpdate.getLastName());
        userPersisted.setPhone(userToUpdate.getPhone());

        userService.save(userPersisted);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Realiza troca de senha do usuário com base em e-mail cadastrado.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Troca de senha realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar troca de senha de usuário.")
    })
    @PutMapping("/{email}/change-password")
    public ResponseEntity<Void> updatePassword(Authentication authentication,
                                               @PathVariable final String email,
                                               @RequestBody @Valid UpdatePasswordRequest updatePasswordRequest){
        validateEmail(email, authentication);

        User userPersisted = userService.findByEmail(email);

        if (!passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(), userPersisted.getPassword())) {
                                                       throw new IllegalArgumentException("Invalid current password.");
                                                   }

        userPersisted.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));

        userService.save(userPersisted);
        return ResponseEntity.ok().build();
    }

    private void validateEmail(String email, Authentication authentication) {
        if (authentication != null && !email.equals(authentication.getName())) {
            throw new AccessDeniedException("Forbidden. You can only access your own info!");
        }
    }

}