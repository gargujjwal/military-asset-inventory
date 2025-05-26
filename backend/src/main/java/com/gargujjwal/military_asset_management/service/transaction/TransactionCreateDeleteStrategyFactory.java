package com.gargujjwal.military_asset_management.service.transaction;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionCreateDeleteStrategyFactory {
  private final Map<String, TransactionCreateDeleteStrategy> strategies;

  public TransactionCreateDeleteStrategy getStrategy(String strategyType) {
    TransactionCreateDeleteStrategy strategy = strategies.get(strategyType);
    if (strategy == null) {
      throw new IllegalArgumentException("Unknown strategy type: " + strategyType);
    }
    return strategy;
  }
}
