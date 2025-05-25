import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import {
  deleteEquipmentMutation,
  getAllEquipmentCategoriesQuery,
} from "~/lib/tanstack-query";
import type { ErrorResponse } from "~/types/backend-stubs";
import { ApiError } from "~/utils/error";
import FormError from "../ui/form-error";
import Loading from "../ui/loading";

export default function AllEquipmentTable() {
  const { data, isPending, isError, error } = useQuery({
    ...getAllEquipmentCategoriesQuery,
  });
  const [errorWhileDel, setErrorWhileDel] = useState<ErrorResponse | null>(
    null
  );
  const queryClient = useQueryClient();
  const deleteMutation = useMutation({
    ...deleteEquipmentMutation,
    onSuccess() {
      setErrorWhileDel(null);
      queryClient.invalidateQueries({
        queryKey: deleteEquipmentMutation.invalidateKeys,
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
  let cnt = 1;
  return (
    <div className="overflow-x-auto rounded-box border border-base-content/5 bg-base-100">
      {errorWhileDel && <FormError error={errorWhileDel} />}
      <table className="table">
        {/* head */}
        <thead>
          <tr>
            <th></th>
            <th>Name</th>
            <th>Category</th>
            <th>Unit of Measure</th>
            <th>Description</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {data.data
            .map((cat) =>
              cat.equipments.map((equip) => (
                <tr key={equip.id}>
                  <th>{cnt++}</th>
                  <td>{equip.name}</td>
                  <td>{cat.name}</td>
                  <td>{cat.unitOfMeasure}</td>
                  <td>{equip.description}</td>
                  <td>
                    <button
                      type="button"
                      className="btn btn-danger"
                      onClick={() => deleteMutation.mutate(equip.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            )
            .flat()}
        </tbody>
      </table>
    </div>
  );
}
