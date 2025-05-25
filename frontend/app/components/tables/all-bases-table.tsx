import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { deleteBaseMutation, getAllBasesQuery } from "~/lib/tanstack-query";
import type { ErrorResponse } from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";
import FormError from "../ui/form-error";
import Loading from "../ui/loading";

export default function AllBasesTable() {
  const { data, isPending, isError, error } = useQuery({ ...getAllBasesQuery });
  const [errorWhileDel, setErrorWhileDel] = useState<ErrorResponse | null>(
    null
  );
  const queryClient = useQueryClient();
  const deleteMutation = useMutation({
    ...deleteBaseMutation,
    onSuccess() {
      setErrorWhileDel(null);
      queryClient.invalidateQueries({
        queryKey: deleteBaseMutation.invalidateKeys,
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
            <th>Name</th>
            <th>Location</th>
            <th>Created At</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {data.data.map((base, i) => (
            <tr key={base.id}>
              <th>{i + 1}</th>
              <td>{base.name}</td>
              <td>{base.location}</td>
              <td>{base.createdAt}</td>
              <td>
                <button
                  type="button"
                  className="btn btn-danger"
                  onClick={() => deleteMutation.mutate(base.id)}
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
