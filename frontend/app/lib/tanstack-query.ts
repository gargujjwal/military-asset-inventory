import {
  changePassword,
  createNewUser,
  login,
  logout,
  me,
} from "~/services/auth-service";
import {
  assignBaseToUser,
  createBase,
  deleteBase,
  getAllBases,
  getBaseById,
} from "~/services/base-service";
import {
  getFilteredDashboard,
  getInitalDashboard,
} from "~/services/dashboard-service";
import {
  createEquipment,
  createEquipmentCategory,
  deleteEquipment,
  deleteEquipmentCategory,
  getAllEquipmentCategories,
  getEquipmentDetailById,
} from "~/services/equipment-service";
import {
  createTransaction,
  deleteTransactions,
  getAllTransactions,
  getAllTransactionsByBase,
  getFilteredTransactions,
} from "~/services/inventory-service";
import { deleteUser, getAllUsers } from "~/services/user-service";
import type {
  EquipmentDto,
  InventoryTransactionDto,
  InventoryTransactionFilter,
} from "~/types/backend-stubs";

/* User Service */
export const getAllUsersQuery = {
  queryKey: ["users"],
  queryFn: getAllUsers,
} as const;

export const deleteUserMutation = {
  mutationKey: ["deleteUser"],
  mutationFn: deleteUser,
  invalidateKeys: ["users"],
} as const;

export const createUserMutation = {
  mutationKey: ["createUser"],
  mutationFn: createNewUser,
  invalidateKeys: ["users"],
} as const;

/* Inventory Transactions */
export const getAllTransactionsQuery = {
  queryKey: ["transactions"],
  queryFn: getAllTransactions,
} as const;

export const getTransactionsByBaseQuery = (baseId: string) =>
  ({
    queryKey: ["transactions", "base", baseId],
    queryFn: () => getAllTransactionsByBase(baseId),
  } as const);

export const getFilteredTransactionsQuery = (
  filter: InventoryTransactionFilter
) =>
  ({
    queryKey: ["transactions", "filtered", filter],
    queryFn: () => getFilteredTransactions(filter),
  } as const);

export const createTransactionMutation = {
  mutationKey: ["createTransaction"],
  mutationFn: (dat: { baseId: string; transaction: InventoryTransactionDto }) =>
    createTransaction(dat.baseId, dat.transaction),
  invalidateKeys: ["transactions", "dashboard"],
} as const;

export const deleteTransactionMutation = {
  mutationKey: ["deleteTransaction"],
  mutationFn: deleteTransactions,
  invalidateKeys: ["transactions", "dashboard"],
} as const;

/* Equipment */
export const getAllEquipmentCategoriesQuery = {
  queryKey: ["equipmentCategories"],
  queryFn: getAllEquipmentCategories,
} as const;

export const getEquipmentDetailQuery = (id: string) =>
  ({
    queryKey: ["equipment", id],
    queryFn: () => getEquipmentDetailById(id),
  } as const);

export const createEquipmentCategoryMutation = {
  mutationKey: ["createEquipmentCategory"],
  mutationFn: createEquipmentCategory,
  invalidateKeys: ["equipmentCategories"],
} as const;

export const createEquipmentMutation = {
  mutationKey: ["createEquipment"],
  mutationFn: (data: { categoryId: string; equipment: EquipmentDto }) =>
    createEquipment(data.categoryId, data.equipment),
  invalidateKeys: ["equipmentCategories"],
} as const;

export const deleteEquipmentCagtegoryMutation = {
  mutationKey: ["deleteEquipmentCategory"],
  mutationFn: deleteEquipmentCategory,
  invalidateKeys: ["equipmentCategories"],
};

export const deleteEquipmentMutation = {
  mutationKey: ["deleteEquipment"],
  mutationFn: deleteEquipment,
  invalidateKeys: ["equipmentCategories"],
} as const;

/* Bases */
export const getAllBasesQuery = {
  queryKey: ["bases"],
  queryFn: getAllBases,
} as const;

export const getBaseByIdQuery = (id: string) =>
  ({
    queryKey: ["base", id],
    queryFn: () => getBaseById(id),
  } as const);

export const createBaseMutation = {
  mutationKey: ["createBase"],
  mutationFn: createBase,
  invalidateKeys: ["bases"],
} as const;

export const deleteBaseMutation = {
  mutationKey: ["deleteBase"],
  mutationFn: deleteBase,
  invalidateKeys: ["bases"],
} as const;

export const assignBaseToUserMutation = {
  mutationKey: ["assignBaseToUser"],
  mutationFn: (dat: { baseId: string; username: string }) =>
    assignBaseToUser(dat.baseId, dat.username),
  invalidateKeys: ["bases", "users"],
} as const;

/* Auth */
export const loginMutation = {
  mutationKey: ["login"],
  mutationFn: login,
  invalidateKeys: ["users", "auth"],
} as const;

export const logoutMutation = {
  mutationKey: ["logout"],
  mutationFn: logout,
  invalidateKeys: ["auth"],
} as const;

export const changePasswordMutation = {
  mutationKey: ["changePassword"],
  mutationFn: changePassword,
  invalidateKeys: ["users"],
} as const;

export const meQuery = {
  queryKey: ["me", "auth"],
  queryFn: me,
} as const;

/* Dashboard */
export const initialDashboardQuery = {
  mutationKey: ["dashboard"],
  mutationFn: getInitalDashboard,
} as const;

export const filteredDashboardQuery = {
  mutationKey: ["dashboard", "filtered"],
  mutationFn: getFilteredDashboard,
} as const;
