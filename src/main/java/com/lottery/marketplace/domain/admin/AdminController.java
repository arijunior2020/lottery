package com.lottery.marketplace.domain.admin;

import com.lottery.marketplace.domain.auth.UserRole;
import com.lottery.marketplace.domain.coupon.*;
import com.lottery.marketplace.domain.lottery.*;
import com.lottery.marketplace.domain.ticket.Ticket;
import com.lottery.marketplace.domain.ticket.TicketService;
import com.lottery.marketplace.domain.ticket.TicketStatus;
import com.lottery.marketplace.domain.user.User;
import com.lottery.marketplace.domain.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name="Admin (administração)")
class AdminController {

    private final LotteryService lotteryService;

    private final LotteryMapper lotteryMapper;

    private final TicketService ticketService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final CouponService couponService;

    private final CouponMapper couponMapper;

    @Operation(summary = "Realiza a criação de um cupom de desconto.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Criação de cupom de desconto realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Access Denied.")
    })
    @PostMapping("/coupon")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CreateCouponRequest coupon) {
      try {
        return ResponseEntity.ok(couponService.saveCoupon(couponMapper.toEntity(coupon)));
      } catch (Exception e) {
        throw new ValidationException(e.getMessage());
      }
    }

    @Operation(summary = "Realiza a alteração de um  registro de cupom de desconto.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Alteração de cupom de desconto realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Access Denied.")
    })
    @PutMapping("/coupon/{id}")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable String id, @RequestBody CreateCouponUpdateRequest coupon) {
      try {
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(couponService.updateCoupon(couponMapper.toEntity(coupon), uuid));
      } catch (Exception e) {
        throw new ValidationException(e.getMessage());
      }
    }

    @Operation(summary = "Realiza a deleção de um  registro de cupom de desconto.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deleção de cupom de desconto realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Access Denied.")
    })
    @DeleteMapping ("/coupon/{id}")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public ResponseEntity<Coupon> deleteCoupon(@PathVariable String id) {
      try {
        UUID uuid = UUID.fromString(id);
        couponService.deleteCoupon(uuid);
        return ResponseEntity.ok().build();
      } catch (Exception e) {
        throw new ValidationException(e.getMessage());
      }
    }

    @Operation(summary = "Realiza a listagem dos cupons de desconto.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de cupons de desconto recuperada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Access Denied.")
    })
    @GetMapping("/coupon")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public ResponseEntity<List<CouponResponse>> findAll() {
      return ResponseEntity.ok(couponService
        .findAll()
        .stream()
        .map(couponMapper::toResponse).toList());
    }

    @Operation(summary = "Realiza a consulta de um cupom de desconto com base no ID.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Access Denied.")
    })
    @GetMapping("/coupon/{id}")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public ResponseEntity<CouponResponse> findById(@PathVariable String id) {
      return ResponseEntity.ok(couponMapper.toResponse(couponService.getCoupon(UUID.fromString(id)).get()));
    }

    @Operation(summary = "Realiza criação de um sorteio.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Criação de sorteio realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Falha na criação de sorteio.")
    })
    @PostMapping("/lottery")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public ResponseEntity<Lottery> createLottery(@RequestBody @Valid CreateLotteryRequest lottery){
        return ResponseEntity.ok(lotteryService.saveLottery(lotteryMapper.toEntity(lottery)));
    }

    @Operation(summary = "Realiza alteração nos dados de um sorteio.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Alteração nos dados do sorteio realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar alteração nos dados de sorteio.")
    })
    @PutMapping("/lottery")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public ResponseEntity<Lottery> updateLottery(@RequestBody @Valid Lottery lottery){
        return ResponseEntity.ok(lotteryService.saveLottery(lottery));
    }

    @Operation(summary = "Realiza exclusão de um sorteio com base em ID.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Exclusão de sorteio realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar exclusão de sorteio.")
    })
    @DeleteMapping("/lottery/{lotteryId}")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public ResponseEntity<Lottery> deleteLottery(@PathVariable String lotteryId){
        lotteryService.deleteLottery(UUID.fromString(lotteryId));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Realiza alteração de senha de usuário administrador.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Alteração de senha realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar alteração de senha.")
    })
    @PostMapping("/user/change-password")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public ResponseEntity<Lottery> changePassword(@RequestBody @Valid UpdatePasswordAdminRequest request){
        User userPersisted = userService.findByEmail(request.getEmail());
        userPersisted.setPassword(passwordEncoder.encode(request.getNewPassword()));

        if(!UserRole.ROLE_ADMIN.equals(userPersisted.getRole())){
            userService.save(userPersisted);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Realiza a listagem de todos os sorteios com base em filtros.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar busca de sorteios.")
    })
    @GetMapping("/lotteries")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public Page<LotteryAdminResponse> getAllLotteries(@Valid LotteryFilter filter,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "executionDate") String sortBy,
                                                      @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = "asc".equalsIgnoreCase(direction) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        return lotteryService.getLotteriesAdmin(filter, PageRequest.of(page, size, sort));
    }

    @Operation(summary = "Realiza a emissão de um relatório de tickets no formato XSLX.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Exportação realizada com sucesso"),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar exportação.")
    })
    @GetMapping("/lottery/{lotteryId}/report")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public void getTicketsXlsx(@PathVariable UUID lotteryId, HttpServletResponse response) throws IOException {
        String filename = generateFilenameBasedOnTime();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".xlsx\"");
        ticketService.exportLotteryTicketsToXlsx(lotteryId, response.getOutputStream());
    }

    @Operation(summary = "Realiza a listagem de tickets com base em número de sorteio.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao realizar busca por e-mail.")
    })
    @GetMapping("/ticket/{ticketNumber}/lottery/{lotteryNumber}")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    public List<Ticket> getTicketsByTicketNumber(@PathVariable String ticketNumber, @PathVariable Long lotteryNumber) {
        return ticketService.findByTicketNumberAndLotteryNumber(ticketNumber, lotteryNumber);
    }

    @Operation(summary = "Realiza a seleção de um vencedor com base em ID de ticket.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Seleção de vencedor realizada com sucesso."),
      @ApiResponse(responseCode = "500", description = "Erro ao seleção de vencedor.")
    })
    @PostMapping("/ticket/{ticketId}/winner")
    @PreAuthorize("hasRole(T(com.lottery.marketplace.domain.auth.UserRole).ROLE_ADMIN.name())")
    @Transactional
    public ResponseEntity<Lottery> selectWinner(@PathVariable String ticketId){
        Ticket ticket = ticketService.findById(UUID.fromString(ticketId)).orElseThrow(() -> new IllegalStateException("Cannot find ticket"));
        Lottery lottery = lotteryService.getLotteryById(ticket.getLotteryId());
        lottery.setStatus(LotteryStatus.FINISHED);
        ticket.setStatus(TicketStatus.WINNER);
        ticketService.save(ticket);
        lotteryService.saveLottery(lottery);
        return ResponseEntity.ok().build();
    }

    private String generateFilenameBasedOnTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "tickets_" + now.format(formatter);
    }

}