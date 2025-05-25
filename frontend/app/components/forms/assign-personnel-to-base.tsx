import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import {
  getAllBasesQuery,
  getAllUsersQuery,
  assignBaseToUserMutation as mut,
} from "~/lib/tanstack-query";
import type { ErrorResponse } from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";
import FormError from "../ui/form-error";
import Loading from "../ui/loading";

type TForm = {
  baseId: string;
  username: string;
};

export default function AssignPersonnelToBaseForm() {
  const queryClient = useQueryClient();
  const assignBaseToUserMutation = useMutation({
    ...mut,
    onSuccess: () => {
      clearErrors();
      queryClient.invalidateQueries({
        queryKey: mut.invalidateKeys,
      });
      reset();
    },
    onError(err) {
      if (err instanceof ApiError) {
        setError("root", err.response);
      }
    },
  });
  const userQuery = useQuery({
    ...getAllUsersQuery,
  });
  const baseQuery = useQuery({
    ...getAllBasesQuery,
  });
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    setError,
    clearErrors,
  } = useForm<TForm>();
  const onSubmit = (data: TForm) => {
    assignBaseToUserMutation.mutate(data);
  };

  if (userQuery.isPending || baseQuery.isPending) {
    return <Loading />;
  }
  if (userQuery.isError || baseQuery.isError) {
    return (
      <FormError
        error={(userQuery.error ?? baseQuery.error) as unknown as ErrorResponse}
      />
    );
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      {errors.root && (
        <FormError error={errors.root as unknown as ErrorResponse} />
      )}
      <div className="mb-6">
        <label
          htmlFor="personnel"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Personnel
        </label>
        <select
          id="personnel"
          {...register("username", {
            required: "User is necessary",
          })}
          className="select select-bordered w-full"
        >
          {userQuery.data.data
            .filter((u) => u.role !== "ADMIN")
            .map((user) => (
              <option key={user.id} value={user.id}>
                {user.fullName}
              </option>
            ))}
        </select>
        {errors.username && (
          <p className="text-error text-sm mt-1">{errors.username.message}</p>
        )}
      </div>

      <div className="mb-6">
        <label
          htmlFor="base"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Base
        </label>
        <select
          id="base"
          {...register("baseId", {
            required: "Base is necessary",
          })}
          className="select select-bordered w-full"
        >
          {baseQuery.data.data.map((base) => (
            <option key={base.id} value={base.id}>
              {base.name}
            </option>
          ))}
        </select>
        {errors.baseId && (
          <p className="text-error text-sm mt-1">{errors.baseId.message}</p>
        )}
      </div>

      <div className="flex justify-end">
        <button
          type="submit"
          disabled={assignBaseToUserMutation.isPending}
          className="btn btn-primary"
        >
          {assignBaseToUserMutation.isPending ? (
            <span className="loading loading-spinner"></span>
          ) : (
            <>Assign User</>
          )}
        </button>
      </div>
    </form>
  );
}
