import { AddressDto } from "./address-dto";
import { BloodpressureRequestDto } from "./bloodpressure-request-dto";
import { Gender } from "./gender";

export interface PatientViewModel {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  gender: Gender;
  birthDate: string;
  age: number;
  isDeleted?: boolean;
  bloodpressureRequestDtoList?: BloodpressureRequestDto[];
  address: AddressDto;
}