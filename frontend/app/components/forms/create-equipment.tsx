import { useQueryClient, useMutation, useQuery } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import {
  createEquipmentMutation,
  getAllEquipmentCategoriesQuery,
} from "~/lib/tanstack-query";
import type { EquipmentDto, ErrorResponse } from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";
import FormError from "../ui/form-error";
import Loading from "../ui/loading";

export default function CreateEquipmentForm() {
  const queryClient = useQueryClient();
  const equipCategoryQuery = useQuery({
    ...getAllEquipmentCategoriesQuery,
  });
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    setError,
    clearErrors,
  } = useForm<EquipmentDto & { categoryId: string }>();
  const { mutate: createEquipment, isPending } = useMutation({
    ...createEquipmentMutation,
    onSuccess: () => {
      clearErrors();
      queryClient.invalidateQueries({
        queryKey: createEquipmentMutation.invalidateKeys,
      });
      reset();
    },
    onError(err) {
      if (err instanceof ApiError) {
        setError("root", err.response);
      }
    },
  });
  const onSubmit = (data: EquipmentDto & { categoryId?: string }) => {
    const categoryId = data.categoryId!;
    delete data.categoryId;
    createEquipment({ categoryId: categoryId, equipment: data });
  };

  if (equipCategoryQuery.isPending) {
    return <Loading />;
  }
  if (equipCategoryQuery.isError) {
    return (
      <FormError error={equipCategoryQuery.error as unknown as ErrorResponse} />
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      {errors.root && (
        <FormError error={errors.root as unknown as ErrorResponse} />
      )}
      <div className="mb-4">
        <label
          htmlFor="name"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Equipment Name
        </label>
        <input
          id="name"
          type="text"
          {...register("name", { required: "Category name is required" })}
          className={`input input-bordered w-full ${
            errors.name ? "input-error" : ""
          }`}
          placeholder="Enter name (e.g., M4, AK47)"
        />
        {errors.name && (
          <p className="text-error text-sm mt-1">{errors.name.message}</p>
        )}
      </div>

      <div className="mb-4">
        <label
          htmlFor="description"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Description
        </label>
        <textarea
          id="description"
          {...register("description")}
          className="textarea textarea-bordered w-full h-24"
          placeholder="Enter description"
        ></textarea>
      </div>

      <div className="mb-6">
        <label
          htmlFor="category"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Equipment Category
        </label>
        <select
          id="category"
          {...register("categoryId", {
            required: "Category is necessary",
          })}
          className="select select-bordered w-full"
        >
          {equipCategoryQuery.data.data.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>
        {errors.categoryId && (
          <p className="text-error text-sm mt-1">{errors.categoryId.message}</p>
        )}
      </div>

      <div className="flex justify-end">
        <button type="submit" disabled={isPending} className="btn btn-primary">
          {isPending ? (
            <span className="loading loading-spinner"></span>
          ) : (
            <>Create Equipment</>
          )}
        </button>
      </div>
    </form>
  );
}
