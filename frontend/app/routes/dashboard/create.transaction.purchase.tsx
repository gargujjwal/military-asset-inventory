import PurchaseTransactionForm from "~/components/forms/transaction/create-purchase-transaction-form";
import TransactionTableWithFilter from "~/components/transactions/transaction-table-with-filter";
import { formatDateToJavaStyle } from "~/utils/date";

export default function PurchaseTransactionPage() {
  return (
    <div className="min-h-screen bg-base-200 p-4 md:p-6 lg:p-8">
      <div className="max-w-7xl mx-auto space-y-8">
        <div className="text-center">
          <h1 className="text-3xl md:text-4xl font-bold text-base-content mb-2">
            Purchase Transactions
          </h1>
          <p className="text-base-content/70">
            Create purchase transactions and manage inventory acquisitions
          </p>
        </div>

        <PurchaseTransactionForm />

        <TransactionTableWithFilter
          initialFilter={{
            endDate: formatDateToJavaStyle(new Date()),
          }}
        />
      </div>
    </div>
  );
}
