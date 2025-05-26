package com.gargujjwal.military_asset_management.exception;

public class InverseTransferTransactionNotFound extends RuntimeException {
  public InverseTransferTransactionNotFound(String message) {
    super(message);
  }

  public InverseTransferTransactionNotFound(String message, Throwable cause) {
    super(message, cause);
  }
}
