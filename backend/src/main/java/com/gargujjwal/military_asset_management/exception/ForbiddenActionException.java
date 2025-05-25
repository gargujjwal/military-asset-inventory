package com.gargujjwal.military_asset_management.exception;

public class ForbiddenActionException extends RuntimeException {
  public ForbiddenActionException(String message) {
    super(message);
  }

  public ForbiddenActionException(String message, Throwable cause) {
    super(message, cause);
  }
}
