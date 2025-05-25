import type {
  EquipmentCategoryDetailDto,
  EquipmentCategoryDto,
  EquipmentDetailDto,
  EquipmentDto,
} from "~/types/backend-stubs";
import { fetchWithAuth } from "~/utils/api";

export function getAllEquipmentCategories() {
  return fetchWithAuth<EquipmentCategoryDetailDto[]>("/equipments/categories");
}

export function getEquipmentDetailById(id: string) {
  return fetchWithAuth<EquipmentDetailDto>(`/equipments/${id}`);
}

export function createEquipmentCategory(
  newEquipmentCategory: EquipmentCategoryDto
) {
  return fetchWithAuth<null>("/equipments/categories", {
    body: JSON.stringify(newEquipmentCategory),
    method: "POST",
  });
}

export function createEquipment(
  categoryId: string,
  newEquipment: EquipmentDto
) {
  return fetchWithAuth<null>(
    `/equipments/categories/${categoryId}/equipments`,
    {
      body: JSON.stringify(newEquipment),
      method: "POST",
    }
  );
}

export function deleteEquipmentCategory(id: string) {
  return fetchWithAuth<null>(`/equipments/categories/${id}`, {
    method: "DELETE",
  });
}

export function deleteEquipment(id: string) {
  return fetchWithAuth<null>(`/equipments/${id}`, {
    method: "DELETE",
  });
}
