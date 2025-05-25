import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import toast from "react-hot-toast";
import { BaseDashboardCard } from "~/components/dashboard/base-dashboard-card";
import { DashboardFilter } from "~/components/dashboard/dashboard-filter";
import {
  filteredDashboardQuery,
  getAllBasesQuery,
  getAllEquipmentCategoriesQuery,
  initialDashboardQuery,
} from "~/lib/tanstack-query";
import type { InventoryTransactionFilter } from "~/types/backend-stubs";

export default function DashboardPage() {
  const [filter, setFilter] = useState<InventoryTransactionFilter>({});
  const [hasFilter, setHasFilter] = useState(false);

  // Fetch base data for filters
  const { data: bases } = useQuery(getAllBasesQuery);
  const { data: equipmentCategories } = useQuery(
    getAllEquipmentCategoriesQuery
  );

  // Fetch dashboard data - initial or filtered
  const {
    data: dashboardData,
    isLoading,
    error,
  } = useQuery({
    queryKey: hasFilter ? ["dashboard", "filtered", filter] : ["dashboard"],
    queryFn: () =>
      hasFilter
        ? filteredDashboardQuery.mutationFn(filter)
        : initialDashboardQuery.mutationFn(),
  });

  const handleFilter = (newFilter: InventoryTransactionFilter) => {
    setFilter(newFilter);
    setHasFilter(Object.keys(newFilter).length > 0);
  };

  if (error) {
    toast.error(error.message);
    return (
      <div className="min-h-screen bg-base-200 p-4 md:p-6 lg:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="alert alert-error">
            <span>Error loading dashboard data. Please try again.</span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-base-200 p-4 md:p-6 lg:p-8">
      <div className="max-w-7xl mx-auto space-y-8">
        {/* Page Header */}
        <div className="text-center">
          <h1 className="text-4xl md:text-5xl font-bold text-base-content mb-2">
            Inventory Dashboard
          </h1>
          <p className="text-lg text-base-content/70">
            Monitor your equipment inventory across all bases
          </p>
        </div>

        {/* Filters */}
        {bases && equipmentCategories && (
          <DashboardFilter
            onFilter={handleFilter}
            bases={bases.data}
            equipmentCategories={equipmentCategories.data}
            isLoading={isLoading}
          />
        )}

        {/* Loading State */}
        {isLoading && (
          <div className="flex justify-center items-center py-12">
            <span className="loading loading-spinner loading-lg"></span>
          </div>
        )}

        {/* Dashboard Cards */}
        {!isLoading && (
          <div className="space-y-8">
            {dashboardData &&
              (dashboardData.data.length === 0 ? (
                <div className="text-center py-12">
                  <div className="text-6xl mb-4">📊</div>
                  <h3 className="text-2xl font-bold text-base-content mb-2">
                    No Data Available
                  </h3>
                  <p className="text-base-content/70">
                    {hasFilter
                      ? "Try adjusting your filter criteria"
                      : "No dashboard data found"}
                  </p>
                </div>
              ) : (
                dashboardData.data.map((dashboard) => (
                  <BaseDashboardCard
                    key={dashboard.base.id}
                    dashboard={dashboard}
                  />
                ))
              ))}
          </div>
        )}
      </div>
    </div>
  );
}
