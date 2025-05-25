import { useQueryClient, useMutation } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { createEquipmentCategoryMutation } from "~/lib/tanstack-query";
import type {
  EquipmentCategoryDto,
  ErrorResponse,
} from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";
import FormError from "../ui/form-error";

export default function CreateEquipmentCategoryForm() {
  const queryClient = useQueryClient();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    setError,
    clearErrors,
  } = useForm<EquipmentCategoryDto>({
    defaultValues: {
      unitOfMeasure: "unit",
    },
  });
  const { mutate: createCategory, isPending } = useMutation({
    ...createEquipmentCategoryMutation,
    onSuccess: () => {
      clearErrors();
      queryClient.invalidateQueries({
        queryKey: createEquipmentCategoryMutation.invalidateKeys,
      });
      reset();
    },
    onError(err) {
      if (err instanceof ApiError) {
        setError("root", err.response);
      }
    },
  });

  const onSubmit = (data: EquipmentCategoryDto) => {
    createCategory(data);
  };

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
          Category Name
        </label>
        <input
          id="name"
          type="text"
          {...register("name", { required: "Category name is required" })}
          className={`input input-bordered w-full ${
            errors.name ? "input-error" : ""
          }`}
          placeholder="Enter category name (e.g., Vehicles, Weapons)"
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
          placeholder="Enter category description"
        ></textarea>
      </div>

      <div className="mb-6">
        <label
          htmlFor="unitOfMeasure"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Unit of Measure
        </label>
        <select
          id="unitOfMeasure"
          {...register("unitOfMeasure", {
            required: "Unit of measure is required",
          })}
          className="select select-bordered w-full"
        >
          <option value="unit">Unit</option>
          <option value="box">Box</option>
          <option value="case">Case</option>
          <option value="kg">Kilogram</option>
          <option value="liter">Liter</option>
          <option value="set">Set</option>
          <option value="pair">Pair</option>
        </select>
        {errors.unitOfMeasure && (
          <p className="text-error text-sm mt-1">
            {errors.unitOfMeasure.message}
          </p>
        )}
      </div>

      <div className="flex justify-end">
        <button type="submit" disabled={isPending} className="btn btn-primary">
          {isPending ? (
            <span className="loading loading-spinner"></span>
          ) : (
            <>Create Category</>
          )}
        </button>
      </div>
    </form>
  );
}
