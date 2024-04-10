package com.lottery.marketplace.domain.ticket;

import com.lottery.marketplace.domain.user.User;
import com.lottery.marketplace.domain.user.UserService;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private static final Random RANDOM = new Random();

    private final TicketRepository ticketRepository;

    private final UserService userService;

    private final TicketMapper ticketMapper;

    public String generateTicketNumber(UUID lotteryId, Long amount) {
        String ticketNumber = "";
        for(int i=1; i <= amount;i++){
            String newTicketNumber;
            do {
                newTicketNumber = String.format("%06d", RANDOM.nextInt(999999));
            } while(ticketRepository.existsByTicketNumberAndLotteryId(newTicketNumber, lotteryId));
            ticketNumber = StringUtils.appendIfMissing(ticketNumber, newTicketNumber);
            if(i < amount) {
                ticketNumber = StringUtils.appendIfMissing(ticketNumber, " - ");
            }
        }

        return ticketNumber;
    }

    public List<Ticket> findByTicketNumberAndLotteryNumber(String ticketNumber, Long lotteryNumber){
        return ticketRepository.findTicketsByTicketNumberContainsIgnoreCaseAndLotteryNumber(ticketNumber, lotteryNumber);
    }

    public Optional<Ticket> findById(UUID ticketId){
        return ticketRepository.findById(ticketId);
    }

    @CacheEvict(value = {"TicketService:getTicketCountForLotteryConfirmedAndPending"},
            key = "#ticket.lotteryId",
            condition =
                    "#ticket.status.toString() == T(com.lottery.marketplace.domain.ticket.TicketStatus).PAYMENT_CONFIRMED.toString()" +
                            " || " +
                            "#ticket.status.toString() == T(com.lottery.marketplace.domain.ticket.TicketStatus).PENDING.toString()"
    )
    public Ticket save(Ticket ticket){
        return ticketRepository.save(ticket);
    }

    public Page<TicketResponse> findTicketByEmail(final String email, PageRequest pageRequest){

        User user = userService.findByEmail(email);
        return ticketRepository.findByUserId(user.getId(), pageRequest)
                .map(ticketMapper::toResponse);
    }

    public BigDecimal getSumOfTicketValuesForLotteryConfirmedAndWinner(UUID lotteryId) {
        List<TicketStatus> statuses = List.of(TicketStatus.PAYMENT_CONFIRMED, TicketStatus.WINNER);
        BigDecimal value = BigDecimal.ZERO;
        BigDecimal summed = ticketRepository.sumTicketValueTotalByLotteryId(lotteryId, statuses);
        if(null != summed){
            value = value.add(summed);
        }
        return value;
    }

    public long getTicketCountForLotteryConfirmedAndWinner(UUID lotteryId) {
        List<TicketStatus> statuses = List.of(TicketStatus.PAYMENT_CONFIRMED, TicketStatus.WINNER);
        return ticketRepository.sumTicketAmountByLotteryId(lotteryId, statuses);
    }

    @Cacheable(value = {"TicketService:getTicketCountForLotteryConfirmedAndPending"}, key="#lotteryId")
    public long getTicketCountForLotteryConfirmedAndPending(UUID lotteryId) {
        List<TicketStatus> statuses = List.of(TicketStatus.PAYMENT_CONFIRMED, TicketStatus.PENDING);
        return ticketRepository.sumTicketAmountByLotteryId(lotteryId, statuses);
    }

    @Cacheable(value = {"TicketService:getTop3TicketsWithHighestValue"}, key="#lotteryId")
    public List<Ranking> getTop3TicketsWithHighestValue(UUID lotteryId){
        Pageable topThree = PageRequest.of(0, 3);
        return ticketRepository.findTop3ByLotteryIdOrderByTicketValueTotalDesc(lotteryId, topThree);
    }

    public void exportLotteryTicketsToXlsx(UUID lotteryId, ServletOutputStream outputStream) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tickets");

            addHeaderRow(sheet);

            int pageNumber = 0;
            int rowIndex = 1;
            while (true) {
                Page<Ticket> page = ticketRepository.findByLotteryId(lotteryId, PageRequest.of(pageNumber, 500));
                List<Ticket> tickets = page.getContent();
                if (tickets.isEmpty()) {
                    break;
                }

                for (Ticket ticket : tickets) {
                    Row row = sheet.createRow(rowIndex++);
                    addTicketToRow(ticket, row);
                }

                pageNumber++;
            }

            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException ex) {
            log.error("Error while exporting data to XLSX. ", ex);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                log.error("Error while closing outputStream. ", ex);
            }
        }
    }

    private void addHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);

        headerRow.createCell(1).setCellValue("Valor único de bilhete");
        headerRow.createCell(2).setCellValue("Valor total de bilhetes");
        headerRow.createCell(3).setCellValue("Qtd. de Bilhetes");
        headerRow.createCell(4).setCellValue("Edição do Sorteio");
        headerRow.createCell(5).setCellValue("CPF");
        headerRow.createCell(6).setCellValue("Nome do comprador");
        headerRow.createCell(7).setCellValue("Telefone do comprador");
        headerRow.createCell(8).setCellValue("Email do comprador");
        headerRow.createCell(9).setCellValue("Data de aquisição");
        headerRow.createCell(10).setCellValue("Status de pagamento");
    }

    private void addTicketToRow(Ticket ticket, Row row) {
        row.createCell(1).setCellValue(ticket.getTicketValue() != null ? ticket.getTicketValue().doubleValue(): 0);
        row.createCell(2).setCellValue(ticket.getTicketValueTotal() != null ? ticket.getTicketValueTotal().doubleValue(): 0);
        row.createCell(3).setCellValue(ticket.getTicketAmount());
        row.createCell(4).setCellValue(ticket.getLotteryNumber());
        row.createCell(5).setCellValue(ticket.getUserIdentification());
        row.createCell(6).setCellValue(ticket.getUserFirstName());
        row.createCell(7).setCellValue(ticket.getUserPhone());
        row.createCell(8).setCellValue(ticket.getUserEmail());
        row.createCell(9).setCellValue(ticket.getCreatedDate() != null ? ticket.getCreatedDate().toString(): "");
        row.createCell(10).setCellValue(ticket.getStatus() != null ? ticket.getStatus().name(): "");
    }
}