import { AddressDto } from "./address-dto";
import { BloodpressureRequestDto } from "./bloodpressure-request-dto";
import { Gender } from "./gender";

export interface PatientResponseDto {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  gender: Gender;
  birthDate: string; // YYYY-MM-DD
  isDeleted?: boolean;
  // Achtung: Backend sendet hier nach DTO deine Request-DTO-Liste; wir behandeln sie optional
  bloodpressureRequestDtoList?: BloodpressureRequestDto[];
  address: AddressDto;
}