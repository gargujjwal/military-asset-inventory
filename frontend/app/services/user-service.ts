import type { UserDto } from "~/types/backend-stubs";
import { fetchWithAuth } from "~/utils/api";

export function getAllUsers() {
  return fetchWithAuth<UserDto[]>("/users");
}

export function getUserByUsername(username: string) {
  return fetchWithAuth<UserDto>(`/users/${username}`);
}

export function deleteUser(username: string) {
  return fetchWithAuth<null>(`/users/${username}`, {
    method: "DELETE",
  });
}
