import { useForm } from "react-hook-form";
import { useAuthenticatedUser } from "~/context/auth-context";
import type {
  BaseDto,
  EquipmentCategoryDto,
  InventoryTransactionFilter,
} from "~/types/backend-stubs";

interface DashboardFilterProps {
  onFilter: (filter: InventoryTransactionFilter) => void;
  bases: BaseDto[];
  equipmentCategories: EquipmentCategoryDto[];
  isLoading?: boolean;
}

export function DashboardFilter({
  onFilter,
  bases,
  equipmentCategories,
  isLoading,
}: DashboardFilterProps) {
  const { user } = useAuthenticatedUser();
  const { register, handleSubmit, reset } =
    useForm<InventoryTransactionFilter>();
  const onSubmit = (data: InventoryTransactionFilter) => {
    // Remove empty values
    const filteredData = Object.fromEntries(
      Object.entries(data).filter(
        ([_, value]) => value !== "" && value !== undefined
      )
    );
    onFilter(filteredData);
  };
  const handleClear = () => {
    reset();
    onFilter({});
  };

  return (
    <div className="bg-base-100 p-6 rounded-lg shadow-lg border border-base-300 mb-8">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-xl font-semibold text-base-content">
          Dashboard Filters
        </h3>
        <div className="badge badge-primary">Filter & Analyze</div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm font-medium text-base-content mb-2">
              Start Date
            </label>
            <input
              type="datetime-local"
              {...register("startDate")}
              className="input input-bordered w-full"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-base-content mb-2">
              End Date
            </label>
            <input
              type="datetime-local"
              {...register("endDate")}
              className="input input-bordered w-full"
            />
          </div>
          {user.role === "ADMIN" && (
            <div>
              <label className="block text-sm font-medium text-base-content mb-2">
                Base
              </label>
              <select
                {...register("baseId")}
                className="select select-bordered w-full"
              >
                <option value="">All Bases</option>
                {bases.map((base) => (
                  <option key={base.id} value={base.id}>
                    {base.name}
                  </option>
                ))}
              </select>
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-base-content mb-2">
              Equipment Type
            </label>
            <select
              {...register("equipmentCategoryId")}
              className="select select-bordered w-full"
            >
              <option value="">All Equipment Types</option>
              {equipmentCategories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="flex flex-wrap gap-3 pt-4">
          <button
            type="submit"
            disabled={isLoading}
            className="btn btn-primary"
          >
            {isLoading ? (
              <span className="loading loading-spinner"></span>
            ) : (
              "Apply Filters"
            )}
          </button>
          <button
            type="button"
            onClick={handleClear}
            className="btn btn-outline btn-secondary"
          >
            Clear Filters
          </button>
        </div>
      </form>
    </div>
  );
}
