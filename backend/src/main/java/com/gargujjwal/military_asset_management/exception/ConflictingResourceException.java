package com.gargujjwal.military_asset_management.exception;

public class ConflictingResourceException extends RuntimeException {

  public ConflictingResourceException(String message) {
    super(message);
  }

  public ConflictingResourceException(String message, Throwable cause) {
    super(message, cause);
  }
}
