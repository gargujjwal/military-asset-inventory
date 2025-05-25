import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { useForm } from "react-hook-form";
import FormError from "~/components/ui/form-error";
import { useAuthenticatedUser } from "~/context/auth-context";
import {
  getAllBasesQuery,
  getAllEquipmentCategoriesQuery,
  getFilteredTransactionsQuery,
} from "~/lib/tanstack-query";
import type {
  ErrorResponse,
  InventoryTransactionFilter,
  TransactionGroupedByBaseDto,
} from "~/types/backend-stubs";
import { formatDateToJavaStyle } from "~/utils/date";

type Props = {
  initialFilter: InventoryTransactionFilter;
  onFilter: (transactions: TransactionGroupedByBaseDto[]) => void;
};
export default function TransactionFilter({ initialFilter, onFilter }: Props) {
  const [filter, setFilter] =
    useState<InventoryTransactionFilter>(initialFilter);
  const filterQuery = useQuery({ ...getFilteredTransactionsQuery(filter) });
  const baseQuery = useQuery(getAllBasesQuery);
  const equipmentsQuery = useQuery(getAllEquipmentCategoriesQuery);
  const user = useAuthenticatedUser();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<InventoryTransactionFilter>();

  const onSubmit = (data: InventoryTransactionFilter) => {
    // convert to correct date format
    if (data.startDate) {
      data.startDate = formatDateToJavaStyle(data.startDate);
    }
    if (data.endDate) {
      data.endDate = formatDateToJavaStyle(data.endDate);
    }

    // Remove empty values
    const filteredData = Object.fromEntries(
      Object.entries(data).filter(
        ([_, value]) => value !== "" && value !== undefined
      )
    );
    setFilter(filteredData);
    reset();
  };

  if (filterQuery.data) {
    onFilter(filterQuery.data.data);
  }

  return (
    <div className="bg-base-100 p-6 rounded-lg shadow-lg border border-base-300 mb-6">
      <h3 className="text-lg font-semibold text-base-content mb-4">
        Filter Transactions
      </h3>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {errors.root && (
          <FormError error={errors.root as unknown as ErrorResponse} />
        )}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              Start Date
            </label>
            <input
              type="datetime-local"
              {...register("startDate")}
              className="input input-bordered w-full"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              End Date
            </label>
            <input
              type="datetime-local"
              {...register("endDate")}
              className="input input-bordered w-full"
            />
          </div>

          {user.user.role === "ADMIN" && (
            <div>
              <label className="block text-sm font-medium text-base-content mb-1">
                Base
              </label>
              <select
                {...register("baseId")}
                className="select select-bordered w-full"
              >
                <option value="">All Bases</option>
                {baseQuery.isSuccess &&
                  baseQuery.data.data.map((base) => (
                    <option key={base.id} value={base.id}>
                      {base.name}
                    </option>
                  ))}
              </select>
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              Equipment Category
            </label>
            <select
              {...register("equipmentCategoryId")}
              className="select select-bordered w-full"
            >
              <option value="">All Categories</option>
              {equipmentsQuery.isSuccess &&
                equipmentsQuery.data.data.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              Equipment
            </label>
            <select
              {...register("equipmentId")}
              className="select select-bordered w-full"
            >
              <option value="">All equipment</option>

              {equipmentsQuery.isSuccess &&
                equipmentsQuery.data.data
                  .map((ec) => ec.equipments)
                  .flat()
                  .map((e) => (
                    <option key={e.id} value={e.id}>
                      {e.name}
                    </option>
                  ))}
            </select>
          </div>
        </div>

        <div className="flex flex-wrap gap-2 pt-2">
          <button
            type="submit"
            disabled={filterQuery.isPending}
            className="btn btn-primary"
          >
            {filterQuery.isPending ? (
              <span className="loading loading-spinner"></span>
            ) : (
              "Apply Filter"
            )}
          </button>
          <button
            type="button"
            onClick={() =>
              reset({
                endDate: "",
                equipmentCategoryId: "",
                equipmentId: "",
                startDate: "",
              })
            }
            className="btn btn-outline"
          >
            Clear Filter
          </button>
        </div>
      </form>
    </div>
  );
}
