import type {
  InventoryTransactionDto,
  InventoryTransactionFilter,
  TransactionGroupedByBaseDto,
} from "~/types/backend-stubs";
import { fetchWithAuth } from "~/utils/api";
import { buildSearchParams } from "~/utils/string";

export function getAllTransactions() {
  return fetchWithAuth<TransactionGroupedByBaseDto[]>(
    "/inventory/transactions"
  );
}

export function getAllTransactionsByBase(id: string) {
  return fetchWithAuth<InventoryTransactionDto[]>(
    `/inventory/transactions/base/${id}`
  );
}

export function getFilteredTransactions(filter: InventoryTransactionFilter) {
  return fetchWithAuth<TransactionGroupedByBaseDto[]>(
    "/inventory/transactions/filtered" +
      buildSearchParams(filter as Record<string, string>)
  );
}

export function createTransaction(
  baseId: string,
  transaction: InventoryTransactionDto
) {
  return fetchWithAuth<null>(`/inventory/transactions/base/${baseId}`, {
    body: JSON.stringify(transaction),
    method: "POST",
  });
}

export function deleteTransactions(transactionId: string) {
  return fetchWithAuth<null>(`/inventory/transactions/${transactionId}`, {
    method: "DELETE",
  });
}
