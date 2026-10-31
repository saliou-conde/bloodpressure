import { AddressDto } from "./address-dto";
import { Gender } from "./gender";

export interface PatientRequestDto {
  firstName: string;
  lastName: string;
  email: string;
  gender: Gender;
  birthDate: string; // YYYY-MM-DD
  address: AddressDto;
}