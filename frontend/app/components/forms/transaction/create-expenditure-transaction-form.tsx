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
  type ExpenditureTransactionDto,
} from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";

type TForm = ExpenditureTransactionDto & { baseId: string };

export default function CreateExpenditureTransactionForm() {
  const queryClient = useQueryClient();
  const { user } = useAuthenticatedUser();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    setError,
    clearErrors,
    setValue,
  } = useForm<TForm>({
    defaultValues: {
      baseId: user.role === "ADMIN" ? "" : "current",
    },
  });
  const baseQuery = useQuery(getAllBasesQuery);
  const { mutate: createTransaction, isPending } = useMutation({
    ...createTransactionMutation,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: createTransactionMutation.invalidateKeys,
      });
      reset();
      setValue("baseId", user.role === "ADMIN" ? "" : "current");
    },
    onError: (error) => {
      if (error instanceof ApiError) {
        setError("root", error.response);
      }
    },
  });
  const equipments = useAllEquipments();
  const onSubmit = (data: TForm) => {
    clearErrors();
    const transactionData = {
      ...data,
      transactionType: TransactionType.EXPENDITURE,
      quantityChange: -Math.abs(Number(data.quantityChange)),
    };
    // @ts-ignore
    delete transactionData.baseId;
    createTransaction({ baseId: data.baseId, transaction: transactionData });
  };

  return (
    <div className="bg-base-100 p-6 rounded-lg shadow-lg border border-base-300">
      <h2 className="text-xl font-bold text-base-content mb-6">
        Create Expenditure Transaction
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
              {equipments.status === "success" &&
                equipments.equipments.map((eq) => (
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
              Quantity *
            </label>
            <input
              type="number"
              {...register("quantityChange", {
                required: "Quantity is required",
                min: { value: 1, message: "Quantity must be positive" },
              })}
              className={`input input-bordered w-full ${
                errors.quantityChange ? "input-error" : ""
              }`}
              placeholder="Enter quantity expended"
            />
            {errors.quantityChange && (
              <p className="text-error text-sm mt-1">
                {errors.quantityChange.message}
              </p>
            )}
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-base-content mb-1">
            Reason *
          </label>
          <input
            type="text"
            {...register("reason", { required: "Reason is required" })}
            className={`input input-bordered w-full ${
              errors.reason ? "input-error" : ""
            }`}
            placeholder="Enter reason for expenditure"
          />
          {errors.reason && (
            <p className="text-error text-sm mt-1">{errors.reason.message}</p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-base-content mb-1">
            Description
          </label>
          <textarea
            {...register("description")}
            className="textarea textarea-bordered w-full"
            placeholder="Additional description (optional)"
            rows={3}
          />
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

        <div className="flex justify-end pt-4">
          <button
            type="submit"
            disabled={isPending}
            className="btn btn-primary"
          >
            {isPending ? (
              <span className="loading loading-spinner"></span>
            ) : (
              "Create Expenditure Transaction"
            )}
          </button>
        </div>
      </form>
    </div>
  );
}
