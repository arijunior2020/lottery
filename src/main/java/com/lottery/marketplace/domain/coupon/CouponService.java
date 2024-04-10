package com.lottery.marketplace.domain.coupon;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class CouponService {

  @Autowired
  private CouponRepository repository;

  @Autowired
  private CouponMapper couponMapper;

  public CouponResponse saveCoupon(Coupon coupon) {
    validateCouponName(coupon.getName());
    validateQuantityIsUnlimitedThanSetQuantityNull(coupon);
    CouponResponse response = new CouponResponse();
    if (coupon.getId() == null) {
      coupon.setCreatedDate(ZonedDateTime.now());
      coupon.setUpdatedDate(ZonedDateTime.now());
      coupon.setAppliedQuantity(0L);
      coupon.setRemanescentQuantity(coupon.getQuantity());
      Coupon savedCoupon = repository.save(coupon);
      response = couponMapper.toResponse(savedCoupon);
    }
    return response;
  }

  public CouponResponse updateCoupon(Coupon coupon, UUID id) {
    validateCouponExists(id);
    validateQuantityIsUnlimitedThanSetQuantityNull(coupon);
    Optional<Coupon> optionalCoupon = getCoupon(id);
    optionalCoupon.get().setUpdatedDate(ZonedDateTime.now());
    optionalCoupon.get().setIsUnlimited(coupon.getIsUnlimited());
    optionalCoupon.get().setQuantity(coupon.getQuantity());
    optionalCoupon.get().setCouponStatus(coupon.getCouponStatus());
    optionalCoupon.get().setPeriodStart(coupon.getPeriodStart());
    optionalCoupon.get().setPeriodEnd(coupon.getPeriodEnd());
    Coupon savedCoupon = repository.save(optionalCoupon.get());
    CouponResponse response = couponMapper.toResponse(savedCoupon);
    return response;
  }

  private void validateQuantityIsUnlimitedThanSetQuantityNull(Coupon coupon) {
    if (coupon.getIsUnlimited()) {
      setQuantityNull(coupon);
    }
  }

  private void setQuantityNull(Coupon coupon) {
    coupon.setQuantity(null);
  }

  private void validateCouponExists(UUID id) {
    Optional<Coupon> couponOptional = getCoupon(id);
    if (!couponOptional.isPresent()) {
      throw new ValidationException("Cupom não encontrado.");
    }
  }

  public Optional<Coupon> getCoupon(UUID id) {
    Optional<Coupon> couponOptional = repository.findById(id);
    return couponOptional;
  }

  public Optional<Coupon> getCoupon(String name) {
    Optional<Coupon> couponOptional = repository.findByName(name, CouponStatus.ACTIVE);
    return couponOptional;
  }

  private void validateCouponName(String name) {
    Optional<Coupon> couponOptional = repository.findByName(name, CouponStatus.ACTIVE);
    couponOptional.ifPresent(coupon -> {
      throw new ValidationException("Já existe um cupom de desconto ativo com este nome. Tente novamente.");
    });
  }


  public void deleteCoupon(UUID id) {
    Optional<Coupon> couponOptional = getCoupon(id);
    if (couponOptional.isPresent()) {
      couponOptional.get().setCouponStatus(CouponStatus.INACTIVE);
      repository.save(couponOptional.get());
    }
  }

  public List<Coupon> findAll() {
    return repository.findAllByCouponStatus(CouponStatus.ACTIVE);
  }

  public ResponseEntity<Object> applyCoupon(String couponName) {
    Optional<Coupon> couponOptional = repository.findByName(couponName, CouponStatus.ACTIVE);

    if (couponOptional.isPresent()) {
      Coupon coupon = couponOptional.get();
      try {
        validateCoupon(coupon);
        applyCouponChanges(coupon);
        return ResponseEntity.status(HttpStatus.OK).body("Cupom aplicado com sucesso!");
      } catch (ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      }
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cupom inexistente.");
    }
  }

  private void validateCoupon(Coupon coupon) throws ValidationException {
    if (!validateIfCouponIsExpired(coupon) && couponShouldBeExpired(coupon)) {
      coupon.setIsExpired(true);
      throw new ValidationException("Cupom expirado. Tente novamente com outro.");
    }

    if (!validateQuantityIsUnlimited(coupon) &&
      validateIfAppliedQuantityEqualsToTotalQuantity(coupon) &&
      validateIfRemanescentQuantityEqualsToZero(coupon)) {
      throw new ValidationException("Cupom esgotado. Tente novamente com outro.");
    }
  }

  public void applyCouponChanges(Coupon coupon) {
    coupon.setAppliedQuantity(coupon.getAppliedQuantity() + 1);
    coupon.setRemanescentQuantity(coupon.getRemanescentQuantity() - 1);
    repository.save(coupon);
  }

  public void addAppliedQuantity(Coupon coupon){
    coupon.setAppliedQuantity(coupon.getAppliedQuantity() + 1);
    repository.save(coupon);
  }
  private boolean couponShouldBeExpired(Coupon coupon) {
    return coupon.getPeriodEnd().isBefore(ZonedDateTime.now());
  }

  private Boolean validateIfCouponIsExpired(Coupon coupon) {
    return coupon.getIsExpired();
  }

  private Boolean validateIfRemanescentQuantityEqualsToZero(Coupon coupon) {
    return coupon.getRemanescentQuantity() == 0;
  }

  private Boolean validateIfAppliedQuantityEqualsToTotalQuantity(Coupon coupon) {
    return Objects.equals(coupon.getAppliedQuantity(), coupon.getQuantity());
  }

  private Boolean validateQuantityIsUnlimited(Coupon coupon) {
    return coupon.getIsUnlimited();
  }

  public ResponseEntity<Coupon> findByCouponName(String name) {
    try {
      validateCouponExistsByName(name);
      return ResponseEntity.ok(getCoupon(name).get());
    } catch (Exception e) {
      throw new ValidationException(e.getMessage());
    }
  }

  private void validateCouponExistsByName(String name) {
    Optional<Coupon> couponOptional = repository.findByName(name, CouponStatus.ACTIVE);
    if (couponOptional.isEmpty()) {
      throw new ValidationException("Cumpom inexistete.");
    }
  }

  public void validateCouponPublic(Coupon coupon) throws ValidationException{
    validateCoupon(coupon);
  }
}
