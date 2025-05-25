import { useQueryClient, useQuery, useMutation } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import FormError from "~/components/ui/form-error";
import { useAuthenticatedUser } from "~/context/auth-context";
import useAllEquipments from "~/hooks/use-all-equipments";
import {
  getAllBasesQuery,
  createTransactionMutation,
} from "~/lib/tanstack-query";
import {
  TransactionType,
  TransferType,
  type TransferTransactionDto,
} from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";

type TForm = TransferTransactionDto & {
  baseId: string;
};
export default function TransferTransactionForm() {
  const queryClient = useQueryClient();

  const { user } = useAuthenticatedUser();
  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
    setError,
    clearErrors,
  } = useForm<TForm>({
    defaultValues: {
      baseId: user.role === "ADMIN" ? "" : "current",
    },
  });

  const transferType = watch("type");
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
  const allEquipments = useAllEquipments();
  const onSubmit = (data: TForm) => {
    clearErrors();
    const transactionData = {
      ...data,
      transactionType: TransactionType.TRANSFER,
      quantityChange:
        data.type === TransferType.IN
          ? Math.abs(Number(data.quantityChange))
          : -Math.abs(Number(data.quantityChange)),
    };
    // @ts-ignore
    delete transactionData.baseId;
    createTransaction({ baseId: data.baseId, transaction: transactionData });
  };

  return (
    <div className="bg-base-100 p-6 rounded-lg shadow-lg border border-base-300">
      <h2 className="text-xl font-bold text-base-content mb-6">
        Create Transfer Transaction
      </h2>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {errors.root && <FormError error={errors.root as any} />}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              Transfer Type *
            </label>
            <select
              {...register("type", { required: "Transfer type is required" })}
              className={`select select-bordered w-full ${
                errors.type ? "select-error" : ""
              }`}
            >
              <option value="">Select Transfer Type</option>
              <option value="IN">Transfer In</option>
              <option value="OUT">Transfer Out</option>
            </select>
            {errors.type && (
              <p className="text-error text-sm mt-1">{errors.type.message}</p>
            )}
          </div>

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
              {allEquipments.status === "success" &&
                allEquipments.equipments.map((eq) => (
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
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-base-content mb-1">
              {transferType === "IN" ? "Source Base *" : "Destination Base *"}
            </label>
            <select
              {...register(
                transferType === "IN" ? "sourceBase.id" : "destBase.id",
                {
                  required: "Base is required",
                }
              )}
              className={`select select-bordered w-full ${
                errors.sourceBase?.id || errors.destBase?.id
                  ? "select-error"
                  : ""
              }`}
            >
              <option value="">Select Base</option>
              {baseQuery.isSuccess &&
                baseQuery.data.data.map((base) => (
                  <option key={base.id} value={base.id}>
                    {base.name}
                  </option>
                ))}
            </select>
            {(errors.sourceBase?.id || errors.destBase?.id) && (
              <p className="text-error text-sm mt-1">Base is required</p>
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
              placeholder="Enter quantity to transfer"
            />
            {errors.quantityChange && (
              <p className="text-error text-sm mt-1">
                {errors.quantityChange.message}
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
              "Create Transfer Transaction"
            )}
          </button>
        </div>
      </form>
    </div>
  );
}
