import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import FormError from "~/components/ui/form-error";
import { createBaseMutation } from "~/lib/tanstack-query";
import type { BaseDto, ErrorResponse } from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";

export default function CreateBaseForm() {
  const queryClient = useQueryClient();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    setError,
    clearErrors,
  } = useForm<BaseDto>();
  const { mutate: createBase, isPending } = useMutation({
    ...createBaseMutation,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: createBaseMutation.invalidateKeys,
      });
      reset();
    },
    onError: (error) => {
      if (error instanceof ApiError) {
        setError("root", error.response);
      }
    },
  });
  const onSubmit = (data: BaseDto) => {
    clearErrors();
    createBase(data);
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
          Base Name
        </label>
        <input
          id="name"
          type="text"
          {...register("name", { required: "Base name is required" })}
          className={`input input-bordered w-full ${
            errors.name ? "input-error" : ""
          }`}
          placeholder="Enter base name (e.g., Fort Alpha)"
        />
        {errors.name && (
          <p className="text-error text-sm mt-1">{errors.name.message}</p>
        )}
      </div>

      <div className="mb-6">
        <label
          htmlFor="location"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Location
        </label>
        <input
          id="location"
          type="text"
          {...register("location", { required: "Location is required" })}
          className={`input input-bordered w-full ${
            errors.location ? "input-error" : ""
          }`}
          placeholder="Enter location (e.g., Northern Command)"
        />
        {errors.location && (
          <p className="text-error text-sm mt-1">{errors.location.message}</p>
        )}
      </div>

      <div className="flex justify-end">
        <button type="submit" disabled={isPending} className="btn btn-primary">
          {isPending ? (
            <span className="loading loading-spinner"></span>
          ) : (
            <>Create Base</>
          )}
        </button>
      </div>
    </form>
  );
}
