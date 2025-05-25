import { env } from "~/env";
import type {
  AccessTokenResponse,
  ApiResponse,
  ErrorResponse,
  SuccessResponse,
} from "~/types/backend-stubs";
import { ApiError, BaseError, ensureError, RefreshAuthError } from "./error";

export class AccessToken {
  private static readonly ACCESS_TOKEN_KEY = "access-token";

  public static getAccessToken(): string | null {
    return localStorage.getItem(AccessToken.ACCESS_TOKEN_KEY);
  }

  public static setAccessToken(token: string): void {
    localStorage.setItem(AccessToken.ACCESS_TOKEN_KEY, token);
  }

  public static clearAccessToken(): void {
    localStorage.removeItem(AccessToken.ACCESS_TOKEN_KEY);
  }
}

export function isErrorResponse(res: unknown): res is ErrorResponse {
  return (
    res !== null &&
    typeof res === "object" &&
    "message" in res &&
    "errors" in res
  );
}

export function isSuccessResponse<T>(res: unknown): res is SuccessResponse<T> {
  return res !== null && typeof res === "object" && "data" in res;
}

async function refreshSession() {
  try {
    const res = await fetch(`${env.API_BASE_URL}/auth/refresh-access-token`, {
      method: "GET",
      credentials: "include",
    });
    const jsonResponse = (await res.json()) as ApiResponse<AccessTokenResponse>;

    if (!res.ok) {
      throw new BaseError("Failed to refresh session", {
        context: jsonResponse,
      });
    }

    // set access token in local storage
    if (isSuccessResponse(jsonResponse)) {
      AccessToken.setAccessToken(jsonResponse.data.accessToken);
      console.log("Access token updated by refresh session");
    }
  } catch (err) {
    const error = ensureError(err);
    throw new RefreshAuthError("Failed to refresh session", error);
  }
}

export async function fetchWithAuth<T>(url: string, options: RequestInit = {}) {
  const accessToken = AccessToken.getAccessToken();

  try {
    const response = await fetch(env.API_BASE_URL + url, {
      ...options,
      headers: {
        ...options.headers,
        "Content-Type": "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (response.status === 401) {
      await refreshSession();

      return fetchWithAuth<T>(url, options);
    }

    return handleApiResponse<T>(response);
  } catch (err) {
    throw ensureError(err);
  }
}

export async function fetchWithoutAuth<T>(
  url: string,
  options: RequestInit = {}
) {
  try {
    const response = await fetch(env.API_BASE_URL + url, {
      ...options,
      headers: { ...options.headers, "Content-Type": "application/json" },
    });

    return handleApiResponse<T>(response);
  } catch (err) {
    throw ensureError(err);
  }
}

async function handleApiResponse<T>(
  response: Response
): Promise<SuccessResponse<T>> {
  let res: ApiResponse<T>;
  if (!response.ok) {
    try {
      res = (await response.json()) as ErrorResponse;
    } catch (error) {
      const err = ensureError(error);

      throw new BaseError("Failed to parse API error response", {
        cause: err,
        context: { endpoint: response.url },
      });
    }

    throw new ApiError(res);
  }

  // return successful response
  return (await response.json()) as SuccessResponse<T>;
}
