package com.lottery.marketplace.domain.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@Tag(name="Coupon (cupom de desconto p/ user não admin)")
public class CouponController {

  @Autowired
  CouponService service;
  @Autowired
  private CouponRepository couponRepository;

  @Operation(summary = "Realiza a aplicação de um cupom de desconto para a compra de um ticket.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Cupom aplicado com sucesso."),
    @ApiResponse(responseCode = "400", description = "Cupom inexistente."),
    @ApiResponse(responseCode = "400", description = "Cupom expirado. Tente novamente com outro."),
    @ApiResponse(responseCode = "400", description = "Cupom esgotado. Tente novamente com outro.")
  })
  @PostMapping("/apply/{couponName}")
  public ResponseEntity<Object> applyCoupon(@PathVariable String couponName) {
    try {
      return service.applyCoupon(couponName);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @Operation(summary = "Realiza uma consulta de cupom de desconto.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200"),
    @ApiResponse(responseCode = "404",description = "Cupom não encontrado")
  })
  @GetMapping("/name/{name}")
  public ResponseEntity<Coupon> findByName(@PathVariable String name) {
    try {
      return service.findByCouponName(name);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
