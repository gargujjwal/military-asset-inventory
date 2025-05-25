import type {
  DashboardDto,
  InventoryTransactionFilter,
} from "~/types/backend-stubs";
import { fetchWithAuth } from "~/utils/api";
import { buildSearchParams } from "~/utils/string";

export function getInitalDashboard() {
  return fetchWithAuth<DashboardDto[]>("/dashboard");
}

export function getFilteredDashboard(filter: InventoryTransactionFilter) {
  return fetchWithAuth<DashboardDto[]>(
    "/dashboard/filtered" + buildSearchParams(filter as Record<string, string>)
  );
}
