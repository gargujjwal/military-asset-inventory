import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import FormError from "~/components/ui/form-error";
import { useAuthenticatedUser } from "~/context/auth-context";
import useAllEquipments from "~/hooks/use-all-equipments";
import {
  createTransactionMutation,
  getAllBasesQuery,
} from "~/lib/tanstack-query";
import {
  TransactionType,
  type AssignmentTransactionDto,
} from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";

type TForm = AssignmentTransactionDto & { baseId: string };

export default function AssignmentTransactionForm() {
  const queryClient = useQueryClient();
  const { user } = useAuthenticatedUser();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    setError,
    clearErrors,
  } = useForm<TForm>({
    defaultValues: {
      baseId: user.role === "ADMIN" ? "" : "current",
    },
  });
  const equipment = useAllEquipments();
  const baseQuery = useQuery(getAllBasesQuery);
  const { mutate: createTransaction, isPending } = useMutation({
    ...createTransactionMutation,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: createTransactionMutation.invalidateKeys,
      });
      reset();
    },
    onError: (error) => {
      if (error instanceof ApiError) {
        setError("root", error.response);
      }
    },
  });

  const onSubmit = (data: TForm) => {
    clearErrors();
    const transactionData = {
      ...data,
      transactionType: TransactionType.ASSIGNMENT,
      quantityChange: -Math.abs(Number(data.quantityAssigned)), // Assignments reduce inventory
    };
    // @ts-ignore
    delete transactionData.baseId;
    createTransaction({ baseId: data.baseId, transaction: transactionData });
  };

  return (
    <div className="bg-base-100 p-6 rounded-lg shadow-lg border border-base-300">
      <h2 className="text-xl font-bold text-base-content mb-6">
        Create Assignment Transaction
      </h2>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {errors.root && <FormError error={errors.root as any} />}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              Equipment *
            </label>
            <select
              {...register("equipment.id", {
                required: "Equipment is required",
              })}
              className={`select select-bordered w-full ${
                errors.equipment?.id ? "select-error" : ""
              }`}
            >
              <option value="">Select Equipment</option>
              {equipment.status === "success" &&
                equipment.equipments.map((eq) => (
                  <option key={eq.id} value={eq.id}>
                    {eq.name}
                  </option>
                ))}
            </select>
            {errors.equipment?.id && (
              <p className="text-error text-sm mt-1">
                {errors.equipment.id.message}
              </p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              Assigned To *
            </label>
            <input
              type="text"
              {...register("assignedTo", { required: "Assignee is required" })}
              className={`input input-bordered w-full ${
                errors.assignedTo ? "input-error" : ""
              }`}
              placeholder="Enter person/unit assigned to"
            />
            {errors.assignedTo && (
              <p className="text-error text-sm mt-1">
                {errors.assignedTo.message}
              </p>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              Quantity Assigned *
            </label>
            <input
              type="number"
              {...register("quantityAssigned", {
                required: "Quantity assigned is required",
                min: { value: 1, message: "Quantity must be positive" },
              })}
              className={`input input-bordered w-full ${
                errors.quantityAssigned ? "input-error" : ""
              }`}
              placeholder="Enter quantity assigned"
            />
            {errors.quantityAssigned && (
              <p className="text-error text-sm mt-1">
                {errors.quantityAssigned.message}
              </p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              Quantity Expended
            </label>
            <input
              type="number"
              {...register("quantityExpended", {
                min: { value: 0, message: "Quantity cannot be negative" },
              })}
              className={`input input-bordered w-full ${
                errors.quantityExpended ? "input-error" : ""
              }`}
              placeholder="Enter quantity expended (optional)"
            />
            {errors.quantityExpended && (
              <p className="text-error text-sm mt-1">
                {errors.quantityExpended.message}
              </p>
            )}
          </div>
        </div>

        {user.role === "ADMIN" && (
          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              Base
            </label>
            <select
              {...register("baseId")}
              className="select select-bordered w-full"
            >
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
            Notes
          </label>
          <textarea
            {...register("notes")}
            className="textarea textarea-bordered w-full"
            placeholder="Additional notes (optional)"
            rows={3}
          />
        </div>

        <div className="flex justify-end pt-4">
          <button
            type="submit"
            disabled={isPending}
            className="btn btn-primary"
          >
            {isPending ? (
              <span className="loading loading-spinner"></span>
            ) : (
              "Create Assignment Transaction"
            )}
          </button>
        </div>
      </form>
    </div>
  );
}
