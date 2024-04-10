package com.lottery.marketplace.domain.lottery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lotteries")
@Tag(name = "Lotteries (sorteios)")
class LotteryController {

    private final LotteryService lotteryService;

    private final LotteryMapper lotteryMapper;


    @Operation(summary = "Realiza a listagem dos sorteios com base nos filtros de página, tamanho de página, " +
      "e ordenação por data.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listagem de sorteios realizada com sucesso."),
      @ApiResponse(responseCode = "400", description = "Parâmetros de busca inválidos."),
      @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso."),
      @ApiResponse(responseCode = "500", description = "Erro desconhecido pelo servidor.")
    })
    @GetMapping
    public Page<LotteryResponse> getAllLotteries(@Valid LotteryFilter filter,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "executionDate") String sortBy,
                                         @RequestParam(defaultValue = "desc") String direction) {

        return lotteryService.getLotteries(filter, page, size, sortBy, direction);
    }

    @Operation(summary = "Realiza a listagem dos três tickets com maior valor do sorteio.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Listagem de ticker com maior valor realizada com sucesso."),
      @ApiResponse(responseCode = "400", description = "ID de sorteio inválido."),
      @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso."),
      @ApiResponse(responseCode = "500", description = "Erro desconhecido pelo servidor.")
    })
    @GetMapping("/{lotteryId}/ranking")
    public ResponseEntity<List<RankingResponse>> getLotteryRankingById(@PathVariable final String lotteryId) {
        final List<RankingResponse> responses = lotteryService.getTop3TicketsWithHighestValue(UUID.fromString(lotteryId));
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Realiza consulta de sorteio por ID.")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Consulta de sorteio realizada com sucesso."),
      @ApiResponse(responseCode = "400", description = "ID de sorteio inválido."),
      @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso."),
      @ApiResponse(responseCode = "500", description = "Erro desconhecido pelo servidor.")
    })
    @GetMapping("/{lotteryId}")
    public ResponseEntity<LotteryResponse> getLotteryById(@PathVariable final String lotteryId) {
        final Lottery lottery = lotteryService.getLotteryById(UUID.fromString(lotteryId));
        return ResponseEntity.ok(lotteryMapper.toResponse(lottery));
    }
}