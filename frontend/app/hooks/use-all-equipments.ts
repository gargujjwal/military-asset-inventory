import { useQuery } from "@tanstack/react-query";
import { getAllEquipmentCategoriesQuery } from "~/lib/tanstack-query";
import type { EquipmentDetailDto } from "~/types/backend-stubs";

type TReturn =
  | { equipments: EquipmentDetailDto[]; status: "success" }
  | { status: "loading" }
  | { status: "error" };

export default function useAllEquipments(): TReturn {
  const { data, status } = useQuery(getAllEquipmentCategoriesQuery);

  if (status === "pending") {
    return { status: "loading" };
  } else if (status === "error") {
    return { status: "error" };
  }
  return {
    equipments: data.data
      .map((ec) =>
        ec.equipments.map((e) => ({
          ...e,
          equipmentCategory: ec,
        }))
      )
      .flat(),
    status: "success",
  };
}
