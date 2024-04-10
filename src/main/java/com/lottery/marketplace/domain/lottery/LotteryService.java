package com.lottery.marketplace.domain.lottery;

import com.lottery.marketplace.domain.ticket.*;
import com.lottery.marketplace.domain.user.User;
import com.lottery.marketplace.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LotteryService {

    private final LotteryRepository lotteryRepository;

    private final UserService userService;

    private final TicketService ticketService;

    private final LotteryMapper lotteryMapper;

    @CacheEvict(value = {"LotteryService:getLotteries", "LotteryService:getLotteryById"}, allEntries = true)
    public Lottery saveLottery(Lottery lottery){
        if(Objects.isNull(lottery.getId())){
            Long maxNumber = getHighestLotteryNumber();
            lottery.setLotteryNumber(maxNumber + 1);
        }
        return lotteryRepository.save(lottery);
    }

    @Transactional
    @CacheEvict(value = {"LotteryService:getLotteries", "LotteryService:getLotteryById"}, allEntries = true)
    public void deleteLottery(UUID lotteryId){
        lotteryRepository.deleteById(lotteryId);
    }

    public Ticket buyTicket(Lottery lottery, final String email, final Long amount) {
        User user = userService.findByEmail(email);

        Page<TicketResponse> listLastTickets = ticketService.findTicketByEmail(email, PageRequest.of(0, 5, Sort.by("createdDate").ascending()));
        long count = listLastTickets.stream().filter(ticketResponse -> TicketStatus.PENDING.equals(ticketResponse.getStatus()))
                .count();
        if(count > 2){
            throw new RuntimeException("You have reached the maximum number of pending tickets.");
        }

        long ticketCountForLottery = ticketService.getTicketCountForLotteryConfirmedAndPending(lottery.getId());
        if(lottery.getTicketQuantity() == ticketCountForLottery){
            throw new RuntimeException("This lottery is not available anymore.");
        }

        if(!LotteryStatus.ACTIVE.equals(lottery.getStatus())) {
            throw new RuntimeException("This lottery is not available.");
        }

        Ticket ticket = buildTicket(amount, lottery, user);

        return ticketService.save(ticket);
    }

    private Ticket buildTicket(Long amount, Lottery lottery, User user) {
        // Find the highest eligible promotion
        Map<String, String> stringPromotions = lottery.getPromotions();

        Map<Integer, Integer> promotions = null;
        if (stringPromotions != null) {
            promotions = new HashMap<>();
            try {
                for (Map.Entry<String, String> entry : stringPromotions.entrySet()) {
                    promotions.put(Integer.valueOf(entry.getKey()), Integer.valueOf(entry.getValue()));
                }
            } catch(NumberFormatException e) {
                promotions = null;
            }
        }

        Map.Entry<Integer, Integer> highestEligiblePromotion = null;
        if (promotions != null) {
            for (Map.Entry<Integer, Integer> entry : promotions.entrySet()) {
                int quantity = entry.getKey();
                if (amount >= quantity) {
                    if (highestEligiblePromotion == null || highestEligiblePromotion.getKey() < quantity) {
                        highestEligiblePromotion = entry;
                    }
                }
            }
        }

        // Calculate discounted value
        BigDecimal discountedTicketValue = lottery.getTicketValue();

        if(highestEligiblePromotion != null) {
            BigDecimal discountPercent = new BigDecimal(highestEligiblePromotion.getValue()).divide(new BigDecimal(100));
            BigDecimal discount = discountedTicketValue.multiply(discountPercent);
            discountedTicketValue = discountedTicketValue.subtract(discount);
        }

        Ticket ticket = new Ticket();
        ticket.setLotteryId(lottery.getId());
        ticket.setLotteryNumber(lottery.getLotteryNumber());
        ticket.setTicketAmount(amount);
        ticket.setUserId(user.getId());
        ticket.setTicketValue(discountedTicketValue.setScale(2, RoundingMode.HALF_DOWN));
        ticket.setTicketValueTotal(discountedTicketValue.multiply(BigDecimal.valueOf(amount)).setScale(2, RoundingMode.HALF_DOWN));
        ticket.setUserIdentification(user.getIdentification());
        ticket.setUserEmail(user.getEmail());
        ticket.setUserFirstName(user.getName());
        ticket.setUserLastName(user.getLastName());
        ticket.setUserPhone(user.getPhone());
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setExpirationDate(ZonedDateTime.now().plusMinutes(lottery.getCheckoutTimeMinutes()));
        return ticket;
    }

    @Cacheable(value = "LotteryService:getLotteries", key = "#page + #size + #sortBy + #direction + #filter")
    public Page<LotteryResponse> getLotteries(LotteryFilter filter, int page, int size, String sortBy, String direction) {
        Sort sort = "asc".equalsIgnoreCase(direction) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<Lottery> spec = Specification.where(null);

        if (filter.getStatusList() != null && !filter.getStatusList().isEmpty()) {
            spec = spec.and(LotterySpecification.hasStatus(filter.getStatusList()));
        }

        return lotteryRepository.findAll(spec, pageRequest)
                .map(lotteryMapper::toResponse);
    }

    @Cacheable(value = "LotteryService:getLotteryById", key = "#lotteryId")
    public Lottery getLotteryById(final UUID lotteryId) {
        return lotteryRepository.findById(lotteryId).orElseThrow(() ->
                new UsernameNotFoundException("Lottery Not Found with -> id : " + lotteryId)
        );
    }

    public List<RankingResponse> getTop3TicketsWithHighestValue(final UUID lotteryId){
        List<Ranking> top3TicketsWithHighestValue = ticketService.getTop3TicketsWithHighestValue(lotteryId);
        List<RankingResponse> rankingResponses = new ArrayList<>();

    for (int i = 0; top3TicketsWithHighestValue.size() > i; i++) {
      rankingResponses.add(RankingResponse.builder()
        .firstName(top3TicketsWithHighestValue.get(i).getFirstName())
        .lastName(top3TicketsWithHighestValue.get(i).getLastName())
        .totalValue(top3TicketsWithHighestValue.get(i).getTotalValue())
        .position(i + 1)
        .build());
    }
    return rankingResponses;
  }

    public Page<LotteryAdminResponse> getLotteriesAdmin(LotteryFilter filter, Pageable pageable) {
        Specification<Lottery> spec = Specification.where(null);

        if (filter.getStatusList() != null && !filter.getStatusList().isEmpty()) {
            spec = spec.and(LotterySpecification.hasStatus(filter.getStatusList()));
        }

        return lotteryRepository.findAll(spec, pageable)
                .map(this::toAdminResponse);
    }

    private LotteryAdminResponse toAdminResponse(Lottery lottery) {
        return LotteryAdminResponse.builder()
                .lottery(lottery)
                .soldTickets(ticketService.getTicketCountForLotteryConfirmedAndWinner(lottery.getId()))
                .soldTicketsValue(ticketService.getSumOfTicketValuesForLotteryConfirmedAndWinner(lottery.getId()))
                .build();
    }

    private Long getHighestLotteryNumber() {
        return lotteryRepository.findHighestLotteryNumber().orElse(0L);
    }
}