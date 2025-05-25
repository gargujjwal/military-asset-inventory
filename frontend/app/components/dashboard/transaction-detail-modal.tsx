import type { InventoryTransactionDto } from "~/types/backend-stubs";

interface TransactionSection {
  title: string;
  transactions: InventoryTransactionDto[];
  value: number;
  color:
    | "primary"
    | "secondary"
    | "accent"
    | "success"
    | "warning"
    | "error"
    | "info";
}

interface TransactionDetailModalProps {
  title: string;
  isOpen: boolean;
  onClose: () => void;
  sections: TransactionSection[];
  netValue?: number;
}

export function TransactionDetailModal({
  title,
  isOpen,
  onClose,
  sections,
  netValue,
}: TransactionDetailModalProps) {
  if (!isOpen) return null;

  const colorClasses = {
    primary: "text-primary border-primary",
    secondary: "text-secondary border-secondary",
    accent: "text-accent border-accent",
    success: "text-success border-success",
    warning: "text-warning border-warning",
    error: "text-error border-error",
    info: "text-info border-info",
  };

  return (
    <div className="modal modal-open">
      <div className="modal-box max-w-4xl max-h-[80vh] overflow-y-auto">
        {/* Modal Header */}
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-base-content">{title}</h2>
          <button className="btn btn-sm btn-circle btn-ghost" onClick={onClose}>
            ✕
          </button>
        </div>

        {/* Net Value Summary (if provided) */}
        {netValue !== undefined && (
          <div className="bg-base-200 rounded-lg p-4 mb-6">
            <div className="text-center">
              <h3 className="text-lg font-semibold text-base-content mb-2">
                Net Movement
              </h3>
              <div
                className={`text-3xl font-bold ${
                  netValue >= 0 ? "text-success" : "text-error"
                }`}
              >
                {netValue >= 0 ? "+" : ""}
                {netValue.toLocaleString()}
              </div>
            </div>
          </div>
        )}

        {/* Transaction Sections */}
        <div className="space-y-6">
          {sections.map((section, index) => (
            <div
              key={index}
              className={`border-l-4 ${colorClasses[section.color]} pl-4`}
            >
              <div className="flex justify-between items-center mb-4">
                <h3
                  className={`text-lg font-semibold ${
                    colorClasses[section.color]
                  }`}
                >
                  {section.title}
                </h3>
                <div
                  className={`badge badge-lg ${
                    section.color === "primary"
                      ? "badge-primary"
                      : section.color === "success"
                      ? "badge-success"
                      : section.color === "error"
                      ? "badge-error"
                      : section.color === "info"
                      ? "badge-info"
                      : "badge-neutral"
                  }`}
                >
                  {section.value.toLocaleString()}
                </div>
              </div>

              {section.transactions.length === 0 ? (
                <p className="text-base-content/60 italic">
                  No transactions found
                </p>
              ) : (
                <div className="overflow-x-auto">
                  <table className="table table-xs table-zebra w-full">
                    <thead>
                      <tr>
                        <th>Date</th>
                        <th>Equipment</th>
                        <th>Quantity</th>
                        <th>Balance</th>
                        <th>Done By</th>
                      </tr>
                    </thead>
                    <tbody>
                      {section.transactions.map((transaction, idx) => (
                        <tr key={idx}>
                          <td className="text-xs">
                            {transaction.transactionDate}
                          </td>
                          <td>
                            <div className="font-medium text-xs">
                              {transaction.equipment.name}
                            </div>
                          </td>
                          <td>
                            <span
                              className={`font-medium text-xs ${
                                transaction.quantityChange > 0
                                  ? "text-success"
                                  : "text-error"
                              }`}
                            >
                              {transaction.quantityChange > 0 ? "+" : ""}
                              {transaction.quantityChange}
                            </span>
                          </td>
                          <td className="text-xs font-medium">
                            {transaction.resultingBalance ?? "N/A"}
                          </td>
                          <td className="text-xs">
                            {transaction.doneBy?.fullName || "Unknown"}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
      <div className="modal-backdrop" onClick={onClose}></div>
    </div>
  );
}
