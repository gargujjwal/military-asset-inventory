package com.gargujjwal.military_asset_management.exception;

public class InventoryNotEnoughException extends RuntimeException {
  public InventoryNotEnoughException(String message) {
    super(message);
  }
}
