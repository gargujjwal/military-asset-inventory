import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { deleteUserMutation, getAllUsersQuery } from "~/lib/tanstack-query";
import type { ErrorResponse } from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";
import FormError from "../ui/form-error";
import Loading from "../ui/loading";

export default function AllUsersTable() {
  const { data, isPending, isError, error } = useQuery({ ...getAllUsersQuery });
  const [errorWhileDel, setErrorWhileDel] = useState<ErrorResponse | null>(
    null
  );
  const queryClient = useQueryClient();
  const deleteMutation = useMutation({
    ...deleteUserMutation,
    onSuccess() {
      setErrorWhileDel(null);
      queryClient.invalidateQueries({
        queryKey: deleteUserMutation.invalidateKeys,
      });
    },
    onError(err) {
      if (err instanceof ApiError) {
        setErrorWhileDel(err.response);
      }
    },
  });

  if (isPending) {
    return <Loading />;
  }
  if (isError) {
    return <FormError error={error as unknown as ErrorResponse} />;
  }

  return (
    <div className="overflow-x-auto rounded-box border border-base-content/5 bg-base-100">
      {errorWhileDel && <FormError error={errorWhileDel} />}
      <table className="table">
        {/* head */}
        <thead>
          <tr>
            <th></th>
            <th>Username</th>
            <th>Full Name</th>
            <th>Role</th>
            <th>Created At</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {data.data.map((user, i) => (
            <tr key={user.id}>
              <th>{i + 1}</th>
              <td>{user.username}</td>
              <td>{user.fullName}</td>
              <td>{user.role}</td>
              <td>{user.createdAt}</td>
              <td>
                <button
                  type="button"
                  className="btn btn-danger"
                  onClick={() => deleteMutation.mutate(user.username)}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
