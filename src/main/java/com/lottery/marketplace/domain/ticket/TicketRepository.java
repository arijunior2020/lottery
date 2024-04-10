package com.lottery.marketplace.domain.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Ticket t WHERE t.ticketNumber LIKE %?1% AND t.lotteryId = ?2")
    boolean existsByTicketNumberAndLotteryId(String ticketNumber, UUID lotteryId);

    Page<Ticket> findByUserId(UUID userId, Pageable pageable);

    Page<Ticket> findByLotteryId(UUID userId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.ticketValueTotal), 0) FROM Ticket t WHERE t.lotteryId = :lotteryId AND t.status IN :statuses")
    BigDecimal sumTicketValueTotalByLotteryId(UUID lotteryId, List<TicketStatus> statuses);

    @Query("SELECT COALESCE(SUM(t.ticketAmount), 0) FROM Ticket t WHERE t.lotteryId = :lotteryId AND t.status IN :statuses")
    long sumTicketAmountByLotteryId(UUID lotteryId, List<TicketStatus> statuses);

//    @Query(
//            "SELECT new com.lottery.marketplace.domain.ticket.Ranking(t.userEmail, SUM(t.ticketValueTotal), t.userFirstName, t.userLastName) " +
//                    "FROM Ticket t " +
//                    "WHERE t.status = 'PAYMENT_CONFIRMED' AND t.lotteryId = :lotteryId " +
//                    "GROUP BY t.userEmail, t.userFirstName, t.userLastName, t.ticketValueTotal " +
//                    "ORDER BY SUM(t.ticketValueTotal) DESC"
//    )
    @Query(value = "SELECT new com.lottery.marketplace.domain.ticket.Ranking(u.email, SUM(t.ticketValueTotal), u.name, u.lastName) " +
      "FROM Ticket t " +
      "JOIN User u ON t.userId = u.id " +
      "WHERE t.status = 'PAYMENT_CONFIRMED' AND t.lotteryId = :lotteryId " +
      "GROUP BY u.email, u.name, u.lastName " +
      "ORDER BY SUM(t.ticketValueTotal) DESC")
    List<Ranking> findTop3ByLotteryIdOrderByTicketValueTotalDesc(UUID lotteryId, Pageable pageable);



    List<Ticket> findTicketsByTicketNumberContainsIgnoreCaseAndLotteryNumber(String ticketNumber, Long lotteryNumber);
}