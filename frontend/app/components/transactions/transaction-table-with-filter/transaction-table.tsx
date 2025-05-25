import { useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";
import { deleteTransactionMutation } from "~/lib/tanstack-query";
import type {
  TransactionGroupedByBaseDto,
  TransactionType,
} from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";

type Props = {
  transactions: TransactionGroupedByBaseDto[];
};
export default function TrasactionTable({ transactions }: Props) {
  const queryClient = useQueryClient();
  const deleteTranasctionMut = useMutation({
    ...deleteTransactionMutation,
    onSuccess() {
      queryClient.invalidateQueries({
        queryKey: deleteTransactionMutation.invalidateKeys,
      });
    },
    onError(err) {
      if (err instanceof ApiError) {
        toast.error(err.response.message);
        err.response.errors.forEach((e) => toast.error(e));
      }
    },
  });

  if (transactions.length === 0) {
    return (
      <div className="bg-base-100 rounded-lg shadow-lg border border-base-300 p-8">
        <div className="text-center text-base-content/70">
          <p className="text-lg font-medium">No transactions found</p>
          <p className="text-sm mt-1">Try adjusting your filter criteria</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-base-100 rounded-lg shadow-lg border border-base-300 overflow-hidden">
      <div className="p-6 border-b border-base-300">
        <h3 className="text-lg font-semibold text-base-content">
          Transaction History
        </h3>
      </div>

      <div className="overflow-x-auto">
        {transactions.map((group) => (
          <div key={group.base.id} className="mb-6 last:mb-0">
            <div className="bg-base-200 px-6 py-3 border-b border-base-300">
              <h4 className="font-semibold text-base-content">
                {group.base.name} - {group.base.location}
              </h4>
            </div>

            <table className="table table-zebra w-full">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Equipment</th>
                  <th>Type</th>
                  <th>Quantity Change</th>
                  <th>Balance</th>
                  <th>Done By</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {group.transactions.map((transaction) => (
                  <tr key={transaction.id} className="hover">
                    <td>
                      <div className="text-sm">
                        {transaction.transactionDate &&
                          transaction.transactionDate}
                      </div>
                    </td>
                    <td>
                      <div className="font-medium">
                        {transaction.equipment.name}
                      </div>
                      {transaction.equipment.description && (
                        <div className="text-sm text-base-content/70">
                          {transaction.equipment.description}
                        </div>
                      )}
                    </td>
                    <td>
                      <span
                        className={`badge ${getTransactionTypeColor(
                          transaction.transactionType
                        )}`}
                      >
                        {transaction.transactionType}
                      </span>
                    </td>
                    <td>
                      <span
                        className={`font-medium ${
                          transaction.quantityChange > 0
                            ? "text-success"
                            : "text-error"
                        }`}
                      >
                        {transaction.quantityChange > 0 ? "+" : ""}
                        {transaction.quantityChange}
                      </span>
                    </td>
                    <td>
                      <span className="font-medium">
                        {transaction.resultingBalance ?? "N/A"}
                      </span>
                    </td>
                    <td>
                      <div className="text-sm">
                        {transaction.doneBy?.fullName || "Unknown"}
                      </div>
                    </td>
                    <td>
                      {transaction.id && (
                        <button
                          onClick={() =>
                            deleteTranasctionMut.mutate(transaction.id!)
                          }
                          disabled={deleteTranasctionMut.isPending}
                          className="btn btn-error btn-sm"
                        >
                          Delete
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ))}
      </div>
    </div>
  );
}

function getTransactionTypeColor(type: TransactionType) {
  switch (type) {
    case "PURCHASE":
      return "badge-success";
    case "TRANSFER":
      return "badge-info";
    case "ASSIGNMENT":
      return "badge-warning";
    case "EXPENDITURE":
      return "badge-error";
    default:
      return "badge-neutral";
  }
}
