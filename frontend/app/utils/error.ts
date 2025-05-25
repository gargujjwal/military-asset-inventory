import type { ErrorResponse } from "~/types/backend-stubs";

export class BaseError extends Error {
  public readonly context?: any;
  public readonly cause?: Error;

  constructor(message: string, options: { cause?: Error; context?: any } = {}) {
    const { cause, context } = options;

    super(message, { cause });
    this.name = this.constructor.name;
    this.context = context;
    this.cause = cause;
  }

  static fromError(error: Error, context?: any) {
    return new BaseError(error.message, { cause: error, context });
  }
}

export class RefreshAuthError extends BaseError {
  constructor(message: string, cause?: Error) {
    super(message, { cause });
  }
}

export class ApiError extends BaseError {
  public readonly response: ErrorResponse;

  constructor(errorResponse: ErrorResponse) {
    super("Error from server", { context: errorResponse });
    this.response = errorResponse;
  }
}

export function ensureError(err: unknown): Error {
  if (err instanceof Error) return err;

  let stringified = "[unable to stringify the thrown value]";

  try {
    stringified = JSON.stringify(err);
  } catch {}

  return new BaseError(
    `The value was thrown as is, not through an Error: ${stringified}`
  );
}
