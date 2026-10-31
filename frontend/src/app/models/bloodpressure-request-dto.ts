
export interface BloodpressureRequestDto {
  patientId: string;
  systole: number;
  diastole: number;
  heartRate: number;
  map?: number | null;
}