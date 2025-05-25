import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { createUserMutation } from "~/lib/tanstack-query";
import { Role, type UserDto } from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";

export default function CreatePersonnelForm() {
  const queryClient = useQueryClient();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    clearErrors,
    setError,
  } = useForm<UserDto>({
    defaultValues: {
      role: Role.LOGISTICS_OFFICER,
    },
  });
  const { mutate: createUser, isPending } = useMutation({
    ...createUserMutation,
    onSuccess: () => {
      clearErrors();
      queryClient.invalidateQueries({
        queryKey: createUserMutation.invalidateKeys,
      });
      reset();
    },
    onError(err) {
      if (err instanceof ApiError) {
        setError("root", err.response);
      }
    },
  });
  const onSubmit = (data: UserDto) => {
    createUser(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
        <div>
          <label
            htmlFor="username"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            Username
          </label>
          <input
            id="username"
            type="text"
            {...register("username", { required: "Username is required" })}
            className={`input input-bordered w-full ${
              errors.username ? "input-error" : ""
            }`}
            placeholder="Enter username"
          />
          {errors.username && (
            <p className="text-error text-sm mt-1">{errors.username.message}</p>
          )}
        </div>
        <div className="mb-4">
          <label
            htmlFor="fullName"
            className="block text-sm font-medium text-gray-700 mb-1"
          >
            Full Name
          </label>
          <input
            id="fullName"
            type="text"
            {...register("fullName", { required: "Full name is required" })}
            className={`input input-bordered w-full ${
              errors.fullName ? "input-error" : ""
            }`}
            placeholder="Enter full name"
          />
          {errors.fullName && (
            <p className="text-error text-sm mt-1">{errors.fullName.message}</p>
          )}
        </div>
      </div>

      <div className="mb-6">
        <label
          htmlFor="role"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Role
        </label>
        <select
          id="role"
          {...register("role", { required: "Role is required" })}
          className="select select-bordered w-full"
        >
          <option value={Role.ADMIN}>Admin</option>
          <option value={Role.BASE_COMMANDER}>Base Commander</option>
          <option value={Role.LOGISTICS_OFFICER}>Logistics Officer</option>
        </select>
        {errors.role && (
          <p className="text-error text-sm mt-1">{errors.role.message}</p>
        )}
      </div>

      <div className="flex justify-end">
        <button type="submit" disabled={isPending} className="btn btn-primary">
          {isPending ? (
            <span className="loading loading-spinner"></span>
          ) : (
            <>Create Personnel</>
          )}
        </button>
      </div>
    </form>
  );
}
