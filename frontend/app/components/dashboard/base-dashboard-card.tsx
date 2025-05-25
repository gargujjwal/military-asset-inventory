import { useState } from "react";
import { TransactionType, type DashboardDto } from "~/types/backend-stubs";
import MetricsCard from "./dashboard-metric-card";
import { TransactionDetailModal } from "./transaction-detail-modal";

interface BaseDashboardCardProps {
  dashboard: DashboardDto;
}

export function BaseDashboardCard({ dashboard }: BaseDashboardCardProps) {
  const [selectedModalType, setSelectedModalType] = useState<string | null>(
    null
  );

  const netMovement =
    dashboard.purchases + dashboard.transferIn - dashboard.transferOut;
  const assigned = dashboard.transactions
    .filter((t) => t.transactionType === TransactionType.ASSIGNMENT)
    .reduce((sum, t) => sum + Math.abs(t.quantityChange), 0);
  const expended = dashboard.transactions
    .filter((t) => t.transactionType === TransactionType.EXPENDITURE)
    .reduce((sum, t) => sum + Math.abs(t.quantityChange), 0);

  const getTransactionsByType = (type: string) => {
    return dashboard.transactions.filter((t) => {
      switch (type) {
        case "purchases":
          return t.transactionType === "PURCHASE";
        case "transferIn":
          return t.transactionType === "TRANSFER" && t.quantityChange > 0;
        case "transferOut":
          return t.transactionType === "TRANSFER" && t.quantityChange < 0;
        default:
          return false;
      }
    });
  };

  const openModal = (type: string) => {
    setSelectedModalType(type);
  };

  const closeModal = () => {
    setSelectedModalType(null);
  };

  return (
    <>
      <div className="bg-base-100 rounded-lg shadow-lg border border-base-300 overflow-hidden">
        {/* Base Header */}
        <div className="p-6 text-black">
          <h2 className="text-2xl font-bold">{dashboard.base.name}</h2>
          <p className="text-black/80">{dashboard.base.location}</p>
        </div>

        {/* Metrics Grid */}
        <div className="p-6">
          <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
            <MetricsCard
              title="Opening Balance"
              value={dashboard.openingBalance}
              color="info"
            />

            <MetricsCard
              title="Closing Balance"
              value={dashboard.closingBalance}
              color="success"
            />

            <MetricsCard
              title="Net Movement"
              value={netMovement}
              subtitle="Click for breakdown"
              color={netMovement >= 0 ? "success" : "error"}
              clickable
              onClick={() => openModal("netMovement")}
            />

            <MetricsCard
              title="Purchases"
              value={dashboard.purchases}
              color="primary"
              clickable
              onClick={() => openModal("purchases")}
            />

            <MetricsCard title="Assigned" value={assigned} color="warning" />

            <MetricsCard title="Expended" value={expended} color="error" />
          </div>

          {/* Additional Transfer Info */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
            <MetricsCard
              title="Transfer In"
              value={dashboard.transferIn}
              color="success"
              clickable
              onClick={() => openModal("transferIn")}
            />

            <MetricsCard
              title="Transfer Out"
              value={dashboard.transferOut}
              color="error"
              clickable
              onClick={() => openModal("transferOut")}
            />
          </div>
        </div>
      </div>

      {/* Modals */}
      {selectedModalType === "netMovement" && (
        <TransactionDetailModal
          title="Net Movement Breakdown"
          isOpen={true}
          onClose={closeModal}
          sections={[
            {
              title: "Purchases",
              transactions: getTransactionsByType("purchases"),
              value: dashboard.purchases,
              color: "success",
            },
            {
              title: "Transfer In",
              transactions: getTransactionsByType("transferIn"),
              value: dashboard.transferIn,
              color: "info",
            },
            {
              title: "Transfer Out",
              transactions: getTransactionsByType("transferOut"),
              value: Math.abs(dashboard.transferOut),
              color: "error",
            },
          ]}
          netValue={netMovement}
        />
      )}

      {selectedModalType && selectedModalType !== "netMovement" && (
        <TransactionDetailModal
          title={`${
            selectedModalType.charAt(0).toUpperCase() +
            selectedModalType.slice(1)
          } Details`}
          isOpen={true}
          onClose={closeModal}
          sections={[
            {
              title:
                selectedModalType.charAt(0).toUpperCase() +
                selectedModalType.slice(1),
              transactions: getTransactionsByType(selectedModalType),
              value:
                selectedModalType === "purchases"
                  ? dashboard.purchases
                  : selectedModalType === "transferIn"
                  ? dashboard.transferIn
                  : Math.abs(dashboard.transferOut),
              color:
                selectedModalType === "purchases"
                  ? "primary"
                  : selectedModalType === "transferIn"
                  ? "success"
                  : "error",
            },
          ]}
        />
      )}
    </>
  );
}
