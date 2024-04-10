package com.lottery.marketplace.domain.lottery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface LotteryRepository extends JpaRepository<Lottery, UUID>, JpaSpecificationExecutor<Lottery> {
    @Query("SELECT MAX(lotteryNumber) FROM Lottery")
    Optional<Long> findHighestLotteryNumber();
}