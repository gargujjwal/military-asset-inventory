import type { BaseDto } from "~/types/backend-stubs";
import { fetchWithAuth } from "~/utils/api";

export function getAllBases() {
  return fetchWithAuth<BaseDto[]>("/bases");
}

export function getBaseById(id: string) {
  return fetchWithAuth<BaseDto>(`/bases/${id}`);
}

export function createBase(newBase: BaseDto) {
  return fetchWithAuth<null>("/bases", {
    body: JSON.stringify(newBase),
    method: "POST",
  });
}

export function deleteBase(id: string) {
  return fetchWithAuth<null>(`/bases/${id}`, {
    method: "DELETE",
  });
}

export function assignBaseToUser(baseId: string, username: string) {
  return fetchWithAuth<null>(`/bases/${baseId}/assign/${username}`, {
    method: "PATCH",
  });
}
