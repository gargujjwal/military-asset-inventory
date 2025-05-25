package com.gargujjwal.military_asset_management.controller;

import com.gargujjwal.military_asset_management.dto.InventoryTransactionDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionFilter;
import com.gargujjwal.military_asset_management.dto.TransactionGroupedByBaseDto;
import com.gargujjwal.military_asset_management.service.EquipmentInventoryTransactionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "/api/inventory",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

  private final Validator validator;

  private final EquipmentInventoryTransactionService inventoryTransactionService;

  @GetMapping(path = "/transactions", consumes = MediaType.ALL_VALUE)
  List<TransactionGroupedByBaseDto> getAllTransactions() {
    return inventoryTransactionService.getAllTransactionsForAllBases();
  }

  @GetMapping(path = "/transactions/base/{baseId}", consumes = MediaType.ALL_VALUE)
  List<InventoryTransactionDto> getAllTransactionsByBase(
      @Valid @NotBlank @PathVariable String baseId) {
    return inventoryTransactionService.getAllTransactionsForBase(baseId);
  }

  @GetMapping(path = "/transactions/filtered", consumes = MediaType.ALL_VALUE)
  List<TransactionGroupedByBaseDto> getFilteredTransactions(
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
          LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
          LocalDateTime endDate,
      @RequestParam(required = false) String baseId,
      @RequestParam(required = false) String equipmentCategoryId,
      @RequestParam(required = false) String equipmentId) {
    var filter =
        InventoryTransactionFilter.builder()
            .startDate(startDate)
            .endDate(endDate)
            .baseId(baseId)
            .equipmentCategoryId(equipmentCategoryId)
            .equipmentId(equipmentId)
            .build();
    log.error(filter.toString());
    return inventoryTransactionService.getFilteredTransactions(filter);
  }

  @PostMapping("/transactions/base/{baseId}")
  void createTransaction(
      @Valid @NotBlank @PathVariable String baseId,
      @RequestBody InventoryTransactionDto inventoryTransactionDto) {
    Set<ConstraintViolation<InventoryTransactionDto>> violations =
        validator.validate(inventoryTransactionDto);
    if (violations.size() > 0) {
      throw new ConstraintViolationException("Validation Failed", violations);
    }
    log.debug("Received DTO: {}", inventoryTransactionDto);
    inventoryTransactionService.createTransaction(inventoryTransactionDto, baseId);
  }

  @DeleteMapping(path = "/transactions/{transactionId}", consumes = MediaType.ALL_VALUE)
  void deleteTransaction(@Valid @NotBlank @PathVariable String transactionId) {
    inventoryTransactionService.deleteTransaction(transactionId);
  }
}
