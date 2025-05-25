import type {
  LoginRequest,
  LoginResponse,
  PasswordChangeReq,
  UserDto,
} from "~/types/backend-stubs";
import { AccessToken, fetchWithAuth, fetchWithoutAuth } from "~/utils/api";

export async function logout() {
  await fetchWithAuth<null>("/auth/logout", { credentials: "include" });
  AccessToken.clearAccessToken();
}

export async function login(loginReq: LoginRequest) {
  const { data } = await fetchWithoutAuth<LoginResponse>("/auth/login", {
    body: JSON.stringify(loginReq),
    method: "POST",
    credentials: "include",
  });
  AccessToken.setAccessToken(data.accessToken);
  return data;
}

export function me() {
  return fetchWithAuth<UserDto>("/auth/me");
}

export function changePassword(
  username: string,
  passwordReq: PasswordChangeReq
) {
  return fetchWithAuth<null>(`/auth/users/${username}/password`, {
    body: JSON.stringify(passwordReq),
    method: "PATCH",
  });
}

export function createNewUser(newUser: UserDto) {
  return fetchWithAuth<null>("/auth/users", {
    body: JSON.stringify(newUser),
    method: "POST",
  });
}
