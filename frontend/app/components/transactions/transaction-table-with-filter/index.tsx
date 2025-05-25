import { useAuthenticatedUser } from "~/context/auth-context";
import TransactionFilter from "./transaction-filter";
import { useState } from "react";
import type {
  InventoryTransactionFilter,
  TransactionGroupedByBaseDto,
} from "~/types/backend-stubs";
import TransactionTable from "./transaction-table";

type Props = { initialFilter: InventoryTransactionFilter };
export default function TransactionTableWithFilter({ initialFilter }: Props) {
  const { user } = useAuthenticatedUser();
  const [transactions, setTransactions] = useState<
    TransactionGroupedByBaseDto[]
  >([]);
  if (user.role !== "ADMIN") {
    initialFilter.baseId = "current";
  }

  return (
    <>
      <TransactionFilter
        initialFilter={initialFilter}
        onFilter={(filteredTrans) => {
          setTransactions(filteredTrans);
        }}
      />
      <TransactionTable transactions={transactions} />
    </>
  );
}
