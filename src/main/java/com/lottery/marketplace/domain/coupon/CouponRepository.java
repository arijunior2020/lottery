package com.lottery.marketplace.domain.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
  @Query("select c from Coupon c where c.name = ?1 and c.couponStatus = ?2")
  Optional<Coupon> findByName(String name, CouponStatus couponStatus);

  @Query("select c from Coupon c where c.couponStatus = :status")
  List<Coupon> findAllByCouponStatus(CouponStatus status);
}
