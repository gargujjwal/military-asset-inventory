export type LocalDateTime = string;

// Enums
export enum TransactionType {
  PURCHASE = "PURCHASE",
  TRANSFER = "TRANSFER",
  ASSIGNMENT = "ASSIGNMENT",
  EXPENDITURE = "EXPENDITURE",
}

export enum TransferType {
  IN = "IN",
  OUT = "OUT",
}

export enum Role {
  ADMIN = "ADMIN",
  BASE_COMMANDER = "BASE_COMMANDER",
  LOGISTICS_OFFICER = "LOGISTICS_OFFICER",
}

export interface AccessTokenResponse {
  accessToken: string;
}

export interface LoginResponse {
  accessToken: string;
  user: UserDto;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface PasswordChangeReq {
  oldPassword: string;
  newPassword: string;
}

export interface SuccessResponse<T> {
  data: T;
  timestamp: LocalDateTime;
}

export interface ErrorResponse {
  message: string;
  errors: string[];
  timestamp: LocalDateTime;
}

export interface BaseDto {
  id: string;
  name: string;
  location: string;
  createdAt: LocalDateTime;
}

export interface EquipmentDto {
  id: string;
  name: string;
  description?: string;
}

export interface EquipmentCategoryDto {
  id: string;
  name: string;
  description?: string;
  unitOfMeasure: string;
}

export interface EquipmentDetailDto {
  id: string;
  name: string;
  description?: string;
  equipmentCategory: EquipmentCategoryDto;
}

export interface EquipmentCategoryDetailDto {
  id: string;
  name: string;
  description?: string;
  unitOfMeasure: string;
  equipments: EquipmentDto[];
}

export interface UserDto {
  id: string;
  username: string;
  fullName: string;
  role: Role;
  createdAt: LocalDateTime;
}

// Transaction hierarchy
export interface InventoryTransactionDto {
  id?: string;
  transactionDate?: LocalDateTime;
  quantityChange: number;
  equipment: EquipmentDto;
  resultingBalance?: number;
  doneBy?: UserDto;
  transactionType: TransactionType;
}

export interface AssignmentTransactionDto extends InventoryTransactionDto {
  assignedTo: string;
  quantityExpended: number;
  quantityAssigned: number;
  notes?: string;
}

export interface ExpenditureTransactionDto extends InventoryTransactionDto {
  reason: string;
  description?: string;
}

export interface PurchaseTransactionDto extends InventoryTransactionDto {
  vendorName: string;
  notes?: string;
}

export interface TransferTransactionDto extends InventoryTransactionDto {
  sourceBase: BaseDto;
  destBase: BaseDto;
  type?: TransferType;
  notes?: string;
}

export interface InventoryTransactionFilter {
  startDate?: LocalDateTime;
  endDate?: LocalDateTime;
  baseId?: string;
  equipmentCategoryId?: string;
  equipmentId?: string;
}

export interface TransactionGroupedByBaseDto {
  base: BaseDto;
  transactions: InventoryTransactionDto[];
}

export interface DashboardDto {
  openingBalance: number;
  closingBalance: number;
  purchases: number;
  transferIn: number;
  transferOut: number;
  base: BaseDto;
  transactions: InventoryTransactionDto[];
}

export type ApiResponse<T> = SuccessResponse<T> | ErrorResponse;
