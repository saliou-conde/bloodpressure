export interface BloodpressureResponseDto {
  id: string;
  systole: number;
  diastole: number;
  heartRate: number;
  map?: number | null;
  createdAt: string; // ISO
  updatedAt?: string | null;
  isDeleted?: boolean;
}